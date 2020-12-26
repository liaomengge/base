package com.github.liaomengge.base_common.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Created by liaomengge on 2018/11/16.
 */
@Data
@Validated
@ConfigurationProperties("base.redis.redisson")
public class RedissonProperties {

    private boolean enabled;
    @NotNull
    private String configLocation;
}
