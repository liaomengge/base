package cn.ly.base_common.utils.properties;

import cn.ly.base_common.utils.log4j2.LyLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/12/18.
 */
@UtilityClass
public class LyConfigUtil {

    private final Logger log = LyLogger.getInstance(LyConfigUtil.class);

    public final String CLASSPATH_FILE_FLAG = "classpath:";

    public Properties loadProperties(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            if (absolutePathStart(fileName)) {
                return loadPropertiesFromAbsoluteFile(fileName);
            } else if (fileName.startsWith(CLASSPATH_FILE_FLAG)) {
                return loadPropertiesFromClasspathFile(fileName);
            } else {
                return loadPropertiesFromRelativeFile(fileName);
            }
        }
        return null;
    }

    private Properties loadPropertiesFromAbsoluteFile(String fileName) {
        Properties properties = null;
        try {

            File file = new File(fileName);
            if (!file.exists()) {
                return null;
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                    StandardCharsets.UTF_8.name()))) {
                properties = new Properties();
                properties.load(bufferedReader);
            }
        } catch (Throwable e) {
            log.warn("load file[" + fileName + "] fail", e);
        }
        return properties;
    }

    private boolean absolutePathStart(String path) {
        File[] files = File.listRoots();
        for (File file : files) {
            if (path.startsWith(file.getPath())) {
                return true;
            }
        }
        return false;
    }

    private Properties loadPropertiesFromClasspathFile(String fileName) {
        fileName = fileName.substring(CLASSPATH_FILE_FLAG.length()).trim();

        List<URL> list = new ArrayList<>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(fileName);
            list = new ArrayList<>();
            while (urls.hasMoreElements()) {
                list.add(urls.nextElement());
            }
        } catch (Throwable e) {
            log.warn("load file[" + fileName + "] fail", e);
        }

        if (list.isEmpty()) {
            return null;
        }

        Properties properties = new Properties();
        for (URL url : list) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(),
                    StandardCharsets.UTF_8.name()))) {
                Properties p = new Properties();
                p.load(bufferedReader);
                properties.putAll(p);
            } catch (Throwable e) {
                log.warn("load file[" + fileName + "] fail", e);
            }
        }
        return properties;
    }

    private Properties loadPropertiesFromRelativeFile(String fileName) {
        String userDir = System.getProperty("user.dir");
        String realFilePath = addSeparator(userDir) + fileName;
        return loadPropertiesFromAbsoluteFile(realFilePath);
    }

    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = LyConfigUtil.class.getClassLoader();
        }
        return classLoader;
    }

    private String addSeparator(String dir) {
        if (!dir.endsWith(File.separator)) {
            dir += File.separator;
        }
        return dir;
    }
}
