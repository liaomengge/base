package cn.ly.base_common.influx.batch;

import cn.ly.base_common.influx.InfluxDBConnection;
import cn.ly.base_common.influx.InfluxDBProperties;
import cn.ly.base_common.influx.consts.InfluxConst;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.thread.LyThreadFactoryBuilderUtil;
import com.google.common.collect.Queues;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/7/21.
 */
public class InfluxBatchHandler {

    private static final Logger log = LyLogger.getInstance(InfluxBatchHandler.class);

    private ExecutorService threadPoolExecutor;
    private LinkedBlockingQueue<Point> linkedBlockingQueue;

    private final InfluxDBConnection influxDBConnection;
    private final InfluxDBProperties influxDBProperties;

    public InfluxBatchHandler(InfluxDBConnection influxDBConnection, InfluxDBProperties influxDBProperties) {
        this.influxDBConnection = influxDBConnection;
        this.influxDBProperties = influxDBProperties;
    }

    public void produce(Point point) {
        boolean status;
        try {
            status = linkedBlockingQueue.offer(point);
        } catch (Exception e) {
            status = false;
        }
        if (!status) {
            try {
                influxDBConnection.getInfluxDB().write(point);
            } catch (Exception e) {
                log.error("write influx fail", e);
            }
        }
    }

    public void consume() {
        List<Point> pointList = new ArrayList<>();
        Queues.drainUninterruptibly(linkedBlockingQueue, pointList, InfluxConst.DEFAULT_TAKE_BATCH_SIZE,
                InfluxConst.DEFAULT_TAKE_BATCH_TIMEOUT, TimeUnit.MILLISECONDS);
        if (!CollectionUtils.isEmpty(pointList)) {
            influxDBConnection.getInfluxDB().write(BatchPoints.database(influxDBProperties.getDb())
                    .points(pointList.stream().toArray(Point[]::new)).build());
        }
    }

    @PostConstruct
    public void init() {
        linkedBlockingQueue = new LinkedBlockingQueue<>(influxDBProperties.getAdditionalConfig().getQueueCapacity());
        int numThreads = influxDBProperties.getAdditionalConfig().getNumThreads();
        if (Objects.isNull(threadPoolExecutor)) {
            threadPoolExecutor = Executors.newFixedThreadPool(numThreads,
                    LyThreadFactoryBuilderUtil.build("influx-metrics"));
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
