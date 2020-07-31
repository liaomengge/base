package cn.ly.base_common.influx;

import cn.ly.base_common.influx.consts.InfluxConst;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/7/21.
 */
@Slf4j
public class InfluxDBConnection {

    @Getter
    private InfluxDB influxDB;
    private InfluxDBProperties influxDBProperties;

    public InfluxDBConnection(InfluxDBProperties influxDBProperties) {
        this.influxDBProperties = influxDBProperties;
    }

    public InfluxDBConnection(InfluxDB influxDB, InfluxDBProperties influxDBProperties) {
        this.influxDB = influxDB;
        this.influxDBProperties = influxDBProperties;
    }

    @PostConstruct
    public void init() {
        if (Objects.isNull(influxDB)) {
            try {
                InfluxDBProperties.AdditionalConfig additionalConfig = influxDBProperties.getAdditionalConfig();
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(additionalConfig.getConnectTimeout().getSeconds(), TimeUnit.SECONDS)
                        .readTimeout(additionalConfig.getReadTimeout().getSeconds(), TimeUnit.SECONDS)
                        .writeTimeout(additionalConfig.getWriteTimeout().getSeconds(), TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .connectionPool(new ConnectionPool(additionalConfig.getMaxConnections(), 5L, TimeUnit.MINUTES));
                influxDB = InfluxDBFactory.connect(influxDBProperties.getUrl(), influxDBProperties.getUser(),
                        influxDBProperties.getPassword(), builder);
            } catch (Exception e) {
                log.error("connect influx db fail", e);
                return;
            }

        }
        try {
            if (!influxDB.databaseExists(influxDBProperties.getDb())) {
                influxDB.createDatabase(influxDBProperties.getDb());
            }
        } catch (Exception e) {
            log.error("create influx db fail", e);
        } finally {
            influxDB.setRetentionPolicy(influxDBProperties.getPolicy());
        }
        influxDB.setLogLevel(InfluxDB.LogLevel.NONE);
        influxDB.enableBatch(InfluxConst.DEFAULT_INFLUX_BATCH_ACTIONS_LIMIT,
                InfluxConst.DEFAULT_INFLUX_BATCH_INTERVAL_DURATION, TimeUnit.MILLISECONDS);
    }

    public void close() {
        if (Objects.nonNull(influxDB)) {
            influxDB.close();
        }
    }
}
