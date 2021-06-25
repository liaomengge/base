package com.github.liaomengge.base_common.support.extension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;

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
import java.util.stream.Collectors;

/**
 * 参照：https://github.com/yu120/neural
 * <p>
 * Created by liaomengge on 2019/10/15.
 */
@Slf4j
public class ExtensionLoader<T> {

    private static final String PREFIX_DEFAULT = "META-INF/";
    private static final String PREFIX_SERVICES = PREFIX_DEFAULT + "services/";

    private Class<T> serviceType;
    private ClassLoader classLoader;
    private Map<String, T> singletonInstances = null;
    private Map<String, Class<T>> extensionClasses = null;
    private static Map<Class<?>, ExtensionLoader<?>> extensionLoaders = new ConcurrentHashMap<>();

    private volatile boolean init = false;

    private ExtensionLoader(Class<T> serviceType, ClassLoader classLoader) {
        this.serviceType = serviceType;
        this.classLoader = classLoader;
    }

    private void checkInit() {
        if (!init) {
            loadExtensionClasses();
        }
    }

    public Class<T> getExtensionClass(String name) {
        this.checkInit();
        return extensionClasses.get(name);
    }

    public T getExtension() {
        SPI spi = serviceType.getAnnotation(SPI.class);
        if (spi.value().length() == 0) {
            throw new RuntimeException(serviceType.getName() + ": The default implementation ID(@SPI.value()) is not " +
                    "set");
        }
        return getExtension(spi.value());
    }

    public T getExtension(String serviceName) {
        if (serviceName == null) {
            return null;
        }
        this.checkInit();

        Class<T> clazz = extensionClasses.get(serviceName);
        if (clazz != null) {
            try {
                SPI spi = serviceType.getAnnotation(SPI.class);
                if (spi.single()) {
                    return this.getSingletonInstance(clazz, serviceName);
                }
                return clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(serviceType.getName() + ": Error when getExtension ", e);
            }
        }
        return null;
    }

    private T getSingletonInstance(Class<T> clazz, String serviceName) throws InstantiationException,
            IllegalAccessException {
        synchronized (singletonInstances) {
            T singleton = singletonInstances.get(serviceName);
            if (singleton == null) {
                singleton = clazz.newInstance();
                singletonInstances.put(serviceName, singleton);
            }
            return singleton;
        }
    }

    public void addExtensionClass(Class<T> clazz) {
        if (clazz == null) {
            return;
        }

        checkInit();
        checkExtensionType(clazz);
        String extensionName = getExtensionName(clazz);
        synchronized (extensionClasses) {
            if (extensionClasses.containsKey(extensionName)) {
                throw new RuntimeException(clazz.getName() + ": Error spiName already exist " + extensionName);
            } else {
                extensionClasses.put(extensionName, clazz);
            }
        }
    }

    private synchronized void loadExtensionClasses() {
        if (init) {
            return;
        }
        extensionClasses = this.loadExtensionClasses(PREFIX_SERVICES);
        singletonInstances = new ConcurrentHashMap<>();
        init = true;
    }

    public static <T> ExtensionLoader<T> getLoader(Class<T> serviceType) {
        return getLoader(serviceType, Thread.currentThread().getContextClassLoader());
    }

    public static <T> ExtensionLoader<T> getLoader(Class<T> serviceType, ClassLoader classLoader) {
        if (serviceType == null) {
            throw new RuntimeException("Error extension serviceType is null");
        }
        if (!serviceType.isAnnotationPresent(SPI.class)) {
            throw new RuntimeException(serviceType.getName() + ": Error extension serviceType without @SPI annotation");
        }

        ExtensionLoader<T> loader = (ExtensionLoader<T>) extensionLoaders.get(serviceType);
        if (loader == null) {
            loader = initExtensionLoader(serviceType, classLoader);
        }

        return loader;
    }

    private static synchronized <T> ExtensionLoader<T> initExtensionLoader(Class<T> serviceType,
                                                                           ClassLoader classLoader) {
        ExtensionLoader<T> loader = (ExtensionLoader<T>) extensionLoaders.get(serviceType);
        if (loader == null) {
            loader = new ExtensionLoader<>(serviceType, classLoader);
            extensionLoaders.putIfAbsent(serviceType, loader);
            loader = (ExtensionLoader<T>) extensionLoaders.get(serviceType);
        }

        return loader;
    }

    public List<T> getExtensions() {
        return this.getExtensions("");
    }

    /**
     * 有些地方需要spi的所有激活的instances, 所以需要能返回一个列表的方法<br>
     * <br>
     * 注意：<br>
     * 1 SpiMeta 中的active 为true<br>
     * 2 按照spiMeta中的order进行排序 <br>
     * <br>
     */
    public List<T> getExtensions(String key) {
        checkInit();
        if (extensionClasses.size() == 0) {
            return Collections.emptyList();
        }

        // 如果只有一个实现, 直接返回
        List<T> extensionList = new ArrayList<>(extensionClasses.size());
        // 多个实现, 按优先级排序返回
        for (Map.Entry<String, Class<T>> entry : extensionClasses.entrySet()) {
            Extension extension = entry.getValue().getAnnotation(Extension.class);
            if (key == null || key.length() == 0) {
                extensionList.add(getExtension(entry.getKey()));
            } else if (extension != null) {
                for (String k : extension.category()) {
                    if (key.equals(k)) {
                        extensionList.add(getExtension(entry.getKey()));
                        break;
                    }
                }
            }
        }

        // order大的排在后面,如果没有设置order的按照设置的顺序
        return extensionList.stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(t -> {
            if (t instanceof Ordered) {
                return OrderUtils.getOrder(t.getClass(), ((Ordered) t).getOrder());
            }
            Extension extension = t.getClass().getAnnotation(Extension.class);
            return Optional.ofNullable(extension).map(val -> OrderUtils.getOrder(t.getClass(), val.order())).orElse(0);
        })).collect(Collectors.toList());
    }

    private void checkExtensionType(Class<T> clazz) {
        // 1) is public class
        if (!serviceType.isAssignableFrom(clazz)) {
            throw new RuntimeException(clazz.getName() + ": Error is not instanceof " + serviceType.getName());
        }

        // 2) contain public constructor and has not-args constructor
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors == null || constructors.length == 0) {
            throw new RuntimeException(clazz.getName() + ": Error has no public no-args constructor");
        }

        for (Constructor<?> constructor : constructors) {
            if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterTypes().length == 0) {
                // 3) check extension clazz instanceof Type.class
                if (!serviceType.isAssignableFrom(clazz)) {
                    throw new RuntimeException(clazz.getName() + ": Error is not instanceof " + serviceType.getName());
                }

                return;
            }
        }

        throw new RuntimeException(clazz.getName() + ": Error has no public no-args constructor");
    }

    private ConcurrentMap<String, Class<T>> loadExtensionClasses(String prefix) {
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

    private ConcurrentMap<String, Class<T>> loadClass(List<String> classNames) {
        ConcurrentMap<String, Class<T>> classMap = new ConcurrentHashMap<>();
        for (String className : classNames) {
            try {
                Class<T> clazz;
                if (classLoader == null) {
                    clazz = (Class<T>) Class.forName(className);
                } else {
                    clazz = (Class<T>) Class.forName(className, false, classLoader);
                }

                this.checkExtensionType(clazz);
                String extensionName = this.getExtensionName(clazz);
                classMap.putIfAbsent(extensionName, clazz);
            } catch (Exception e) {
                log.error(serviceType.getName() + ": Error load extension class", e);
            }
        }

        return classMap;

    }

    private String getExtensionName(Class<?> clazz) {
        Extension extension = clazz.getAnnotation(Extension.class);
        return (extension != null && !"".equals(extension.value())) ? extension.value() : clazz.getSimpleName();
    }

    private void parseUrl(Class<T> serviceType, URL url, List<String> classNames) throws ServiceConfigurationError {
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
            log.error(serviceType.getName() + ": Error reading spi configuration file", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error(serviceType.getName() + ": Error closing spi configuration file", e);
            }
        }
    }

    private void parseLine(Class<T> serviceType, URL url, String line, List<String> names)
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
