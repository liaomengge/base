package com.github.liaomengge.base_common.feign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/8/25.
 */
@Data
@ConfigurationProperties("base.feign")
public class FeignProperties {

    private OkHttpProperties okhttp = new OkHttpProperties();

    @Data
    public static class OkHttpProperties {
        private boolean enabled = false;
        private boolean followRedirects = true;
        private boolean disableSslValidation = false;
        private int maxConnections = 20;
        private long timeToLive = 300L;
        private TimeUnit timeToLiveUnit = TimeUnit.SECONDS;
        private int connectTimeout = 5000;
        private int readTimeout = 5000;
        private int writeTimeout = 5000;
    }
}
