package cn.ly.base_common.redis;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * Created by liaomengge on 2018/11/16.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "ly.redis.redisson")
public class RedissonProperties {

    private boolean enabled;
    @NotNull
    private String configLocation;
}
