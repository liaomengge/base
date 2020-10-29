package com.github.liaomengge.base_common.influx;

import com.github.liaomengge.base_common.influx.consts.InfluxConst;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB.LogLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Created by liaomengge on 2020/7/21.
 */
@Data
@Validated
@ConfigurationProperties("base.influx")
public class InfluxDBProperties {

    private String db;
    private String measurement;
    private String url;
    private String username;
    private String password;
    private String retentionPolicy;
    private String consistencyLevel;
    private LogLevel logLevel = LogLevel.NONE;
    private boolean isGzipEnabled = true;
    private boolean isBatchEnabled = true;
    private AdditionalConfig additionalConfig = new AdditionalConfig();

    @Data
    public static class AdditionalConfig {
        //queue config
        private int numThreads = Runtime.getRuntime().availableProcessors();
        private int queueCapacity = InfluxConst.DEFAULT_QUEUE_CAPACITY;//队列容量应配置大于等于批处理数

        //influx batch config
        private int batchSize = InfluxConst.DEFAULT_TAKE_BATCH_SIZE;
        private int batchTimeout = InfluxConst.DEFAULT_TAKE_BATCH_TIMEOUT;

        //okhttp config
        private int maxConnections = InfluxConst.DEFAULT_MAX_CONNECTIONS;
        private Duration connectTimeout = InfluxConst.DEFAULT_CONNECT_TIMEOUT;
        private Duration readTimeout = InfluxConst.DEFAULT_READ_TIMEOUT;
        private Duration writeTimeout = InfluxConst.DEFAULT_WRITE_TIMEOUT;
    }

    public String getDb() {
        return StringUtils.isNoneBlank(this.db) ? this.db : InfluxConst.DEFAULT_DATABASE;
    }

    public String getMeasurement() {
        return StringUtils.isNoneBlank(this.measurement) ? this.measurement : InfluxConst.DEFAULT_MEASUREMENT;
    }

    public String getRetentionPolicy() {
        return StringUtils.isNoneBlank(this.retentionPolicy) ? this.retentionPolicy : InfluxConst.DEFAULT_INFLUX_POLICY;
    }

    public String getConsistencyLevel() {
        return StringUtils.isNoneBlank(this.consistencyLevel) ? this.consistencyLevel :
                InfluxConst.DEFAULT_CONSISTENCY_LEVEL;
    }
}
