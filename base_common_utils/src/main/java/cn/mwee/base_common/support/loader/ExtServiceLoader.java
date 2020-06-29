package cn.mwee.base_common.support.loader;

import cn.mwee.base_common.support.extension.ExtensionLoader;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by liaomengge on 2019/10/16.
 */
public class ExtServiceLoader {

    private static final Logger logger = MwLogger.getInstance(ExtensionLoader.class);

    private static final String PREFIX_DEFAULT = "META-INF/";
    private static final String PREFIX_SERVICES = PREFIX_DEFAULT + "services/";

    private Class<?> serviceType;
    private ClassLoader classLoader;
    @Getter
    private Map<String, Class<?>> extensionClasses;

    private volatile static ExtServiceLoader extServiceLoader = null;

    public static ExtServiceLoader getInstance(Class<?> serviceType) {
        if (extServiceLoader == null) {
            synchronized (ExtServiceLoader.class) {
                if (extServiceLoader == null) {
                    extServiceLoader = new ExtServiceLoader(serviceType);
                }
            }
        }
        return extServiceLoader;
    }

    private ExtServiceLoader(Class<?> serviceType) {
        this(serviceType, Thread.currentThread().getContextClassLoader());
    }

    private ExtServiceLoader(Class<?> serviceType, ClassLoader classLoader) {
        this.serviceType = serviceType;
        this.classLoader = classLoader;
        this.extensionClasses = loadExtensionClasses(PREFIX_SERVICES);
    }

    private ConcurrentMap<String, Class<?>> loadExtensionClasses(String prefix) {
        String fullName = prefix + serviceType.getName();
        List<String> classNames = new ArrayList<>();

        try {
            Enumeration<URL> urls;
            if (classLoader == null) {
                urls = ClassLoader.getSystemResources(fullName);
            } else {
                urls = classLoader.getResources(fullName);
            }

            if (urls == null || !urls.hasMoreElements()) {
                return new ConcurrentHashMap<>();
            }

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                this.parseUrl(serviceType, url, classNames);
            }
        } catch (Exception e) {
            throw new RuntimeException("ExtensionLoader loadExtensionClasses error, prefix: " +
                    prefix + " serviceType: " + serviceType, e);
        }

        return loadClass(classNames);
    }

    private ConcurrentMap<String, Class<?>> loadClass(List<String> classNames) {
        ConcurrentMap<String, Class<?>> classMap = new ConcurrentHashMap<>();
        for (String className : classNames) {
            try {
                Class<?> clz;
                if (classLoader == null) {
                    clz = Class.forName(className);
                } else {
                    clz = Class.forName(className, false, classLoader);
                }

                this.checkExtensionType(clz);
                classMap.putIfAbsent(className, clz);
            } catch (Exception e) {
                logger.error(serviceType.getName() + ": Error load extension class", e);
            }
        }

        return classMap;

    }

    private void checkExtensionType(Class<?> clz) {
        // 1) is public class
        if (!serviceType.isAssignableFrom(clz)) {
            throw new RuntimeException(clz.getName() + ": Error is not instanceof " + serviceType.getName());
        }

        // 2) contain public constructor and has not-args constructor
        Constructor<?>[] constructors = clz.getConstructors();
        if (constructors == null || constructors.length == 0) {
            throw new RuntimeException(clz.getName() + ": Error has no public no-args constructor");
        }

        for (Constructor<?> constructor : constructors) {
            if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterTypes().length == 0) {
                // 3) check extension clz instanceof Type.class
                if (!serviceType.isAssignableFrom(clz)) {
                    throw new RuntimeException(clz.getName() + ": Error is not instanceof " + serviceType.getName());
                }

                return;
            }
        }

        throw new RuntimeException(clz.getName() + ": Error has no public no-args constructor");
    }

    private void parseUrl(Class<?> serviceType, URL url, List<String> classNames) throws ServiceConfigurationError {
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = url.openStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                this.parseLine(serviceType, url, line, classNames);
            }
        } catch (Exception e) {
            logger.error(serviceType.getName() + ": Error reading spi configuration file", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error(serviceType.getName() + ": Error closing spi configuration file", e);
            }
        }
    }

    private void parseLine(Class<?> serviceType, URL url, String line, List<String> names)
            throws ServiceConfigurationError {
        int ci = line.indexOf('#');
        if (ci >= 0) {
            line = line.substring(0, ci);
        }

        line = line.trim();
        if (line.length() <= 0) {
            return;
        }
        if ((line.indexOf(' ') >= 0) || (line.indexOf('\t') >= 0)) {
            throw new RuntimeException(serviceType.getName() + ": " +
                    url + ":" + line + ": Illegal spi configuration-file syntax: " + line);
        }

        int cp = line.codePointAt(0);
        if (!Character.isJavaIdentifierStart(cp)) {
            throw new RuntimeException(serviceType.getName() + ": " +
                    url + ":" + line + ": Illegal spi provider-class name: " + line);
        }

        for (int i = Character.charCount(cp); i < line.length(); i += Character.charCount(cp)) {
            cp = line.codePointAt(i);
            if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                throw new RuntimeException(serviceType.getName() + ": " +
                        url + ":" + line + ": Illegal spi provider-class name: " + line);
            }
        }

        if (!names.contains(line)) {
            names.add(line);
        }
    }
}
