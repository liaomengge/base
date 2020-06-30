package cn.ly.base_common.retrofit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Created by liaomengge on 2019/3/1.
 */
@Data
@ConfigurationProperties(prefix = "mwee.retrofit")
public class RetrofitProperties {

    private static final int DEFAULT_TIMEOUT_MILLIS = 5_000;//单位：毫秒
    private static final long DEFAULT_KEEP_ALIVE_MIN = 5L;//单位：分钟

    private String projName = "mwee";
    private String messageConverter = "fastjson";
    private final HttpClientProperties http = new HttpClientProperties();
    private final LogProperties log = new LogProperties();
    private final SentinelProperties sentinel = new SentinelProperties();

    @Data
    public static class HttpClientProperties {
        private int maxIdleConnections = 5;
        private long keepAlive = DEFAULT_KEEP_ALIVE_MIN;
        private boolean enabledTraceHeader = true;
        private int connectTimeout = DEFAULT_TIMEOUT_MILLIS;
        private int readTimeout = DEFAULT_TIMEOUT_MILLIS;
        private int writeTimeout = DEFAULT_TIMEOUT_MILLIS;
        private int retryCount = 3;
        private List<UrlHttpClientProperties> urls;
    }

    @Data
    public static class UrlHttpClientProperties {
        private String url;
        private Integer connectTimeout;
        private Integer readTimeout;
        private Integer writeTimeout;
    }

    @Data
    public static class LogProperties {
        private String ignoreMethodName;
    }

    @Data
    public static class SentinelProperties {
        private boolean enabled;
    }
}
