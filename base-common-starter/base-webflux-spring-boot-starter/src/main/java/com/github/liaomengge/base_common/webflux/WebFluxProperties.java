package com.github.liaomengge.base_common.webflux;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Created by liaomengge on 2021/2/8.
 */
@Data
@ConfigurationProperties("base.webflux")
public class WebFluxProperties {

    private WebClientProperties webClient = new WebClientProperties();

    @Data
    public static class WebClientProperties {
        private String baseUrl;
        private Duration connectTimeout = Duration.ofSeconds(5);
        private Duration readTimeout = Duration.ofSeconds(5);
        private Duration writeTimeout = Duration.ofSeconds(5);
    }
}
