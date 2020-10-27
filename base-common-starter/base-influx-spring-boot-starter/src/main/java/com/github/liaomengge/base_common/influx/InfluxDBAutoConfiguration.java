package com.github.liaomengge.base_common.influx;

import com.github.liaomengge.base_common.influx.batch.InfluxBatchHandler;
import com.github.liaomengge.base_common.influx.helper.InfluxHelper;
import lombok.AllArgsConstructor;
import org.influxdb.InfluxDB;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/7/21.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(InfluxDBProperties.class)
@ConditionalOnClass(InfluxDB.class)
public class InfluxDBAutoConfiguration {

    private final InfluxDBProperties influxDBProperties;

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public InfluxDBConnection influxDBConnection() {
        return new InfluxDBConnection(influxDBProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public InfluxBatchHandler influxBatchHandler(InfluxDBConnection influxDBConnection) {
        return new InfluxBatchHandler(influxDBConnection, influxDBProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public InfluxHelper influxHelper(InfluxBatchHandler influxBatchHandler) {
        return new InfluxHelper(influxBatchHandler);
    }
}
