package com.github.liaomengge.base_common.utils.properties;

import com.github.liaomengge.base_common.utils.io.LyIOUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2020/8/1.
 */
@UtilityClass
public class LyResourceUtil {

    private final Logger log = LyLogger.getInstance(LyResourceUtil.class);

    public URL getResource(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResource(resourceName);
    }

    public InputStream getResourceAsStream(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }

    public void loadProperties(String resourceName) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties.load(inputStreamReader);
        } catch (Exception e) {
            log.warn("load file [" + resourceName + "] fail", e);
        } finally {
            LyIOUtil.closeQuietly(inputStreamReader);
        }
    }
}
