package com.github.liaomengge.base_common.ribbon.processor;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * Created by liaomengge on 2020/12/22.
 */
public class RibbonEagerLoadEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Boolean enabled = environment.getProperty("base.ribbon.eager-load.enabled", Boolean.class);
        if (BooleanUtils.toBoolean(enabled)) {
            Map<String, Object> propertiesMap = Maps.newHashMap();
            propertiesMap.put("ribbon.eager-load.enabled", true);
            MapPropertySource mapPropertySource = new MapPropertySource("feignEagerLoadProperties", propertiesMap);
            environment.getPropertySources().addFirst(mapPropertySource);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }
}
