package com.github.liaomengge.base_common.influx.helper;

import com.github.liaomengge.base_common.influx.batch.InfluxBatchHandler;
import com.github.liaomengge.base_common.influx.consts.InfluxConst;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2020/7/21.
 */
public class InfluxHelper {

    private final InfluxBatchHandler influxBatchHandler;

    public InfluxHelper(InfluxBatchHandler influxBatchHandler) {
        this.influxBatchHandler = influxBatchHandler;
    }

    public void write(Map<String, Object> fields) {
        write(new HashMap<>(), fields);
    }

    public void write(String measurement, Map<String, Object> fields) {
        write(measurement, new HashMap<>(), fields);
    }

    public void write(Map<String, String> tags, Map<String, Object> fields) {
        write(InfluxConst.DEFAULT_MEASUREMENT, tags, fields);
    }

    public void write(String measurement, Map<String, String> tags, Map<String, Object> fields) {
        Point.Builder builder = Point.measurement(measurement);
        if (!CollectionUtils.isEmpty(tags)) {
            builder.tag(tags);
        }
        if (!CollectionUtils.isEmpty(fields)) {
            builder.fields(fields);
        }
        builder.time(new NanoClock().nanos(), TimeUnit.NANOSECONDS);
        write(builder.build());
    }

    public <T> void write(T t, Function<T, Point> function) {
        influxBatchHandler.produce(function.apply(t));
    }

    public <T> void write(List<T> list, Function<T, Point> function) {
        List<Point> pointList = list.stream().map(function).collect(Collectors.toList());
        write(BatchPoints.builder().points(pointList).build());
    }

    public void write(Point point) {
        influxBatchHandler.produce(point);
    }

    public void write(List<Point> pointList) {
        write(BatchPoints.builder().points(pointList).build());
    }

    public void write(BatchPoints batchPoints) {
        influxBatchHandler.getInfluxDBConnection().getInfluxDB().write(batchPoints);
    }

    private final class NanoClock {

        private final long EPOCH_NANOS = System.currentTimeMillis() * 1_000_000;
        private final long NANO_START = System.nanoTime();
        private final long OFFSET_NANOS = EPOCH_NANOS - NANO_START;

        public long nanos() {
            return System.nanoTime() + OFFSET_NANOS;
        }
    }
}
