package cn.ly.base_common.metric;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2018/12/19.
 */
@Data
@ConfigurationProperties("ly.metric")
public class MetricProperties {

    private DatasourceProperties datasource;
    private HttpProperties http;
    private ThreadPoolProperties threadpool;
    private WebProperties web;

    @Data
    public class DatasourceProperties {

        private DruidProperties druid;
        private HikariProperties hikari;

        @Data
        public class DruidProperties {
            private boolean enabled = true;
        }

        @Data
        public class HikariProperties {
            private boolean enabled = true;
        }
    }

    @Data
    public class HttpProperties {

        private HttpClientProperties httpclient;
        private Okhttp3Properties okhttp3;

        @Data
        public class HttpClientProperties {
            private boolean enabled = true;
            private int maxHttpRoueCount = 5;//统计多少httpRoute
        }

        @Data
        public class Okhttp3Properties {
            private boolean enabled = true;
        }
    }

    @Data
    public class ThreadPoolProperties {
        private boolean enabled = true;
    }

    @Data
    public class WebProperties {
        
        private TomcatProperties tomcat;
        private UndertowProperties undertow;

        @Data
        public class TomcatProperties {
            private boolean enabled = true;
        }

        @Data
        public class UndertowProperties {
            private boolean enabled = true;
        }
    }
}
