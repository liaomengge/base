package cn.ly.base_common.metric;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2018/12/19.
 */
@Data
@ConfigurationProperties("ly.metric")
public class MetricProperties {

    private CacheProperties cache = new CacheProperties();
    private DatasourceProperties datasource = new DatasourceProperties();
    private HttpProperties http = new HttpProperties();
    private ThreadPoolProperties threadpool = new ThreadPoolProperties();
    private WebProperties web = new WebProperties();

    @Data
    public class CacheProperties {

        private LocalProperties local = new LocalProperties();
        private RedisProperties redis = new RedisProperties();

        @Data
        public class LocalProperties {
            private boolean enabled;
        }

        @Data
        public class RedisProperties {
            private boolean enabled;
        }
    }

    @Data
    public class DatasourceProperties {

        private DruidProperties druid = new DruidProperties();
        private HikariProperties hikari = new HikariProperties();

        @Data
        public class DruidProperties {
            private boolean enabled;
        }

        @Data
        public class HikariProperties {
            private boolean enabled;
        }
    }

    @Data
    public class HttpProperties {

        private HttpClientProperties httpclient = new HttpClientProperties();
        private Okhttp3Properties okhttp3 = new Okhttp3Properties();

        @Data
        public class HttpClientProperties {
            private boolean enabled;
            private int maxHttpRoueCount = 5;//统计多少httpRoute
        }

        @Data
        public class Okhttp3Properties {
            private boolean enabled;
        }
    }

    @Data
    public class ThreadPoolProperties {
        private boolean enabled;
    }

    @Data
    public class WebProperties {

        private TomcatProperties tomcat = new TomcatProperties();
        private UndertowProperties undertow = new UndertowProperties();

        @Data
        public class TomcatProperties {
            private boolean enabled = true;
        }

        @Data
        public class UndertowProperties {
            private boolean enabled;
        }
    }
}
