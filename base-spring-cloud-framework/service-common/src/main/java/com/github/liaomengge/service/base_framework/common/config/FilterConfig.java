package com.github.liaomengge.service.base_framework.common.config;

import lombok.Data;

/**
 * Created by liaomengge on 2018/9/21.
 */
@Data
public class FilterConfig {

    //是否开启默认filter
    private boolean enabledDefaultFilter = true;

    private SignConfig sign = new SignConfig();
    private FailFastConfig failFast = new FailFastConfig();
    private RateLimitConfig rateLimit = new RateLimitConfig();
    private LogConfig log = new LogConfig();

    /**
     * 签名
     */
    @Data
    public static class SignConfig {

        private boolean enabled = true;
        private String ignoreMethodName = "";//以逗号分隔
        private String config = "";//签名配置,格式:{key:value}
    }

    /**
     * 快速失败
     */
    @Data
    public static class FailFastConfig {

        private String methodName = "";//以逗号分隔
    }

    /**
     * 限流
     */
    @Data
    public static class RateLimitConfig {

        private String config = "";//限流配置,格式:{key:value}
    }

    @Data
    public static class LogConfig {

        private String ignoreArgsMethodName = "";//以逗号分隔
        private String ignoreResultMethodName = "";//以逗号分隔
    }
}
