package com.github.liaomengge.base_common.ribbon.processor;

import com.google.common.collect.Maps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * Created by liaomengge on 2020/12/31.
 */
public class RibbonRetryEnvironmentPostProcess implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> propertiesMap = Maps.newHashMap();
        propertiesMap.put("ribbon.MaxAutoRetries", 0);
        propertiesMap.put("ribbon.MaxAutoRetriesNextServer", 0);
        propertiesMap.put("ribbon.OkToRetryOnAllOperations", false);
        MapPropertySource mapPropertySource = new MapPropertySource("ribbonRetryProperties", propertiesMap);
        environment.getPropertySources().addFirst(mapPropertySource);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 9;
    }
}
