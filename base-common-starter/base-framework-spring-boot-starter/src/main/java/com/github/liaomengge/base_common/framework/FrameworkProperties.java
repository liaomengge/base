package com.github.liaomengge.base_common.framework;

import com.github.liaomengge.base_common.framework.consts.FrameworkConst;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Created by liaomengge on 2019/11/6.
 */
@Data
@ConfigurationProperties(prefix = FrameworkConst.CONFIGURATION_PROPERTIES_PREFIX)
public class FrameworkProperties {
    
    private final ControllerAopProperties controllerAop = new ControllerAopProperties();
    private final SentinelProperties sentinel = new SentinelProperties();
    private final CorsProperties cors = new CorsProperties();
    private final XssProperties xss = new XssProperties();

    @Data
    public static class ControllerAopProperties {
        private String[] basePackages;
    }

    @Data
    public static class SentinelProperties {
        private boolean enabled;
    }

    @Data
    public static class CorsProperties {
        private boolean enabled = true;
        private String path = "/**";
        private boolean allowCredentials = true;
        private List<String> allowedOrigins = Collections.singletonList(CorsConfiguration.ALL);
        private List<String> allowedHeaders = Collections.singletonList(CorsConfiguration.ALL);
        private List<String> allowedMethods = Collections.singletonList(CorsConfiguration.ALL);
        private List<String> exposedHeaders = Lists.newArrayList("Content-Type", "X-Requested-With", "Accept",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers");
        private Duration maxAge = Duration.ofMinutes(30);
    }

    @Data
    public static class XssProperties {
        private boolean enabled = true;
        private int order = Ordered.LOWEST_PRECEDENCE;
        private String[] urlPatterns = {"/*"};
    }
}
