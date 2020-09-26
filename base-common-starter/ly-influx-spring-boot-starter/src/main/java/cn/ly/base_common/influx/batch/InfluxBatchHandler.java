package cn.ly.base_common.influx.batch;

import cn.ly.base_common.helper.buffer.BatchBuffer;
import cn.ly.base_common.helper.buffer.BatchBuffer.QueueStrategy;
import cn.ly.base_common.influx.InfluxDBConnection;
import cn.ly.base_common.influx.InfluxDBProperties;
import cn.ly.base_common.influx.consts.InfluxConst;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.thread.LyThreadFactoryBuilderUtil;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;

import lombok.Getter;

/**
 * Created by liaomengge on 2020/7/21.
 */
public class InfluxBatchHandler {

    private static final Logger log = LyLogger.getInstance(InfluxBatchHandler.class);

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
                        try {
                            TimeUnit.MILLISECONDS.sleep(InfluxConst.DEFAULT_TAKE_BATCH_TIMEOUT);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            });

        }
    }
}
