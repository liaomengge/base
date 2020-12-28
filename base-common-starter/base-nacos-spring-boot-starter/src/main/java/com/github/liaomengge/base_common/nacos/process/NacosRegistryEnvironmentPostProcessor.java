package com.github.liaomengge.base_common.nacos.process;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * Created by liaomengge on 2020/12/28.
 */
public class NacosRegistryEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Boolean enabled = environment.getProperty("base.nacos.registry.enabled", Boolean.class, Boolean.FALSE);
        Map<String, Object> propertiesMap = Maps.newHashMap();
        propertiesMap.put("spring.cloud.nacos.discovery.registerEnabled", BooleanUtils.toBoolean(enabled));
        MapPropertySource mapPropertySource = new MapPropertySource("nacosRegistryProperties", propertiesMap);
        environment.getPropertySources().addFirst(mapPropertySource);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }
}
