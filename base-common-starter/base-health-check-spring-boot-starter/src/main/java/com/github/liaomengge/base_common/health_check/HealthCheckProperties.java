package com.github.liaomengge.base_common.health_check;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2019/7/11.
 */
@Data
@ConfigurationProperties(prefix = "base.health-check")
public class HealthCheckProperties {
}
