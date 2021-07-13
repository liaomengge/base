package com.github.liaomengge.base_common.utils.properties;

import com.github.liaomengge.base_common.utils.io.LyIOUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by liaomengge on 2020/8/1.
 */
@Slf4j
@UtilityClass
public class LyResourceUtil {

    public URL getResource(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResource(resourceName);
    }

    public InputStream getResourceAsStream(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }

    public String getResourceAsString(String resourceName) {
        InputStream inputStream = getResourceAsStream(resourceName);
        return LyIOUtil.toString(inputStream);
    }

    public Properties loadProperties(String resourceName) {
        Properties properties = new Properties();
        InputStream inputStream;
        InputStreamReader inputStreamReader = null;
        try {
            inputStream = getResourceAsStream(resourceName);
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties.load(inputStreamReader);
        } catch (Exception e) {
            log.warn("load file[{}] fail", resourceName, e);
        } finally {
            LyIOUtil.closeQuietly(inputStreamReader);
        }
        return properties;
    }

    public Properties loadYml(String resourceName) {
        return loadYml(new ClassPathResource(resourceName));
    }

    public Properties loadYml(EncodedResource encodedResource) {
        return loadYml(encodedResource.getResource());
    }

    public Properties loadYml(Resource... resource) {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}
