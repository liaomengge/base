package com.github.liaomengge.base_common.influx.batch;

import com.github.liaomengge.base_common.helper.buffer.BatchBuffer;
import com.github.liaomengge.base_common.helper.buffer.BatchBuffer.QueueStrategy;
import com.github.liaomengge.base_common.influx.InfluxDBConnection;
import com.github.liaomengge.base_common.influx.InfluxDBProperties;
import com.github.liaomengge.base_common.influx.consts.InfluxConst;
import com.github.liaomengge.base_common.utils.thread.LyThreadFactoryBuilderUtil;
import com.github.liaomengge.base_common.utils.thread.LyThreadUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liaomengge on 2020/7/21.
 */
@Slf4j
public class InfluxBatchHandler {

    private ExecutorService threadPoolExecutor;
    private BatchBuffer<Point> batchBuffer;

    @Getter
    private final InfluxDBConnection influxDBConnection;
    private final InfluxDBProperties influxDBProperties;

    public InfluxBatchHandler(InfluxDBConnection influxDBConnection, InfluxDBProperties influxDBProperties) {
        this.influxDBConnection = influxDBConnection;
        this.influxDBProperties = influxDBProperties;
    }

    public void produce(Point point) {
        batchBuffer.produce(point, val -> {
            try {
                influxDBConnection.getInfluxDB().write(val);
            } catch (Exception e) {
                log.error("write influx fail", e);
            }
        });
    }

    public void consume() {
        InfluxDBProperties.AdditionalConfig additionalConfig = influxDBProperties.getAdditionalConfig();
        batchBuffer.consume(points -> influxDBConnection.getInfluxDB()
                        .write(BatchPoints.database(influxDBProperties.getDb()).points(points).build()),
                additionalConfig.getBatchSize(),
                new QueueStrategy(Duration.ofMillis(additionalConfig.getBatchTimeout())));
    }

    @PostConstruct
    public void init() {
        this.batchBuffer = new BatchBuffer<>(influxDBProperties.getAdditionalConfig().getQueueCapacity());
        int numThreads = influxDBProperties.getAdditionalConfig().getNumThreads();
        if (Objects.isNull(threadPoolExecutor)) {
            threadPoolExecutor = Executors.newFixedThreadPool(numThreads,
                    LyThreadFactoryBuilderUtil.build("influx-consume"));
        }
        for (int i = 0; i < numThreads; i++) {
            threadPoolExecutor.execute(() -> {
                while (true) {
                    try {
                        consume();
                    } catch (Exception e) {
                        log.error("batch write influx fail", e);
                        LyThreadUtil.sleep(InfluxConst.DEFAULT_TAKE_BATCH_TIMEOUT);
                    }
                }
            });

        }
    }
}
