package com.github.liaomengge.base_common.health_check;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2019/7/11.
 */
@Data
@ConfigurationProperties("base.health-check")
public class HealthCheckProperties {
}
