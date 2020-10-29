package com.github.liaomengge.base_common.metric;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2018/12/19.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MetricProperties.class)
public class MetricAutoConfiguration {
}
