package cn.ly.base_common.rest.template;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2018/11/1.
 */
@Data
@ConfigurationProperties(prefix = "ly.rest.template")
public class RestTemplateProperties {

    private static final int DEFAULT_TIMEOUT_MILLIS = 5_000;//单位：毫秒

    private String projName = "ly";
    private String messageConverter = "fastjson";
    private final HttpClientProperties http = new HttpClientProperties();
    private final StatsdProperties statsd = new StatsdProperties();
    private final LogProperties log = new LogProperties();
    private final SentinelProperties sentinel = new SentinelProperties();

    @Data
    public static class HttpClientProperties {
        private int maxTotal = 256;
        private int defaultMaxPerRoute = 128;
        private boolean enabledTraceHeader = true;
        private int connectionTimeout = DEFAULT_TIMEOUT_MILLIS;
        private int readTimeout = DEFAULT_TIMEOUT_MILLIS;
        private int connectionRequestTimeout = DEFAULT_TIMEOUT_MILLIS;
        private int retryCount = 3;
        private List<UrlHttpClientProperties> urls;
    }

    @Data
    public static class UrlHttpClientProperties {
        private String url;
        private Integer maxPerRoute;
        private Integer readTimeout;
    }

    @Data
    public static class StatsdProperties {
        private String prefix;
        private String hostname;
        private int port;
    }

    @Data
    public static class LogProperties {
        private String ignoreMethodName;
    }

    @Data
    public static class SentinelProperties {
        private boolean enabled;
    }

    protected boolean isJacksonMessageConverter() {
        return StringUtils.endsWithIgnoreCase("jackson", this.messageConverter);
    }
}
