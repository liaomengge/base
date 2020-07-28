package cn.ly.base_common.influx.batch;

import cn.ly.base_common.influx.InfluxDBConnection;
import cn.ly.base_common.influx.InfluxDBProperties;
import cn.ly.base_common.influx.consts.InfluxConst;
import cn.ly.base_common.influx.util.QueueUtil;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/7/21.
 */
@Slf4j
public class InfluxBatchHandler {

    private final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    private LinkedBlockingQueue<Point> linkedBlockingQueue =
            new LinkedBlockingQueue<>(InfluxConst.DEFAULT_BLOCKING_QUEUE_SIZE);

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CPU_NUM, CPU_NUM,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(32),
            new ThreadPoolExecutor.CallerRunsPolicy());

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
        QueueUtil.drainUninterruptibly(linkedBlockingQueue, pointList, InfluxConst.DEFAULT_TAKE_BATCH_SIZE,
                InfluxConst.DEFAULT_TAKE_BATCH_TIMEOUT, TimeUnit.MILLISECONDS);
        if (!CollectionUtils.isEmpty(pointList)) {
            influxDBConnection.getInfluxDB().write(BatchPoints.database(influxDBProperties.getDb())
                    .points(pointList.stream().toArray(Point[]::new)).build());
        }
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < CPU_NUM; i++) {
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
