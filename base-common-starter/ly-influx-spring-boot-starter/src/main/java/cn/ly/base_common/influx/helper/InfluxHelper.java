package cn.ly.base_common.influx.helper;

import cn.ly.base_common.influx.batch.InfluxBatchHandler;
import cn.ly.base_common.influx.consts.InfluxConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/7/21.
 */
@Slf4j
public class InfluxHelper {

    private String appId;

    @Autowired(required = false)
    private Environment environment;

    private final InfluxBatchHandler influxBatchHandler;

    public InfluxHelper(InfluxBatchHandler influxBatchHandler) {
        this.influxBatchHandler = influxBatchHandler;
    }

    public void write(Point point) {
        influxBatchHandler.produce(point);
    }

    public void write(Map<String, Object> fields) {
        write(new HashMap<>(), fields);
    }

    public void write(Map<String, String> tags, Map<String, Object> fields) {
        write("", tags, fields);
    }

    public void write(String eventName, Map<String, Object> fields) {
        write(eventName, new HashMap<>(), fields);
    }

    public void write(String eventName, Map<String, String> tags, Map<String, Object> fields) {
        Point.Builder builder = Point.measurement(InfluxConst.DEFAULT_MEASUREMENT).tag("appId", appId);
        if (StringUtils.isNoneBlank(eventName)) {
            builder.tag("eventName", eventName);
        }
        if (!CollectionUtils.isEmpty(tags)) {
            builder.tag(tags);
        }
        if (!CollectionUtils.isEmpty(fields)) {
            builder.fields(fields);
        }
        builder.time(new NanoClock().nanos(), TimeUnit.NANOSECONDS);
        write(builder.build());
    }

    /**
     * apollo app.id配置
     */
    @PostConstruct
    public void init() {
        if (Objects.nonNull(environment)) {
            appId = environment.getProperty("app.id");
        }
        if (StringUtils.isBlank(appId)) {
            Properties properties = new Properties();
            InputStream in = null;
            try {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/app.properties");
                if (Objects.nonNull(in)) {
                    properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                    appId = properties.getProperty("app.id");
                }
            } catch (Exception e) {
                log.warn("load app.properties fail", e);
            } finally {
                if (Objects.nonNull(in)) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private final class NanoClock implements Serializable {
        private static final long serialVersionUID = -1928869833619757243L;

        private final long EPOCH_NANOS = System.currentTimeMillis() * 1_000_000;
        private final long NANO_START = System.nanoTime();
        private final long OFFSET_NANOS = EPOCH_NANOS - NANO_START;

        public long nanos() {
            return System.nanoTime() + OFFSET_NANOS;
        }
    }
}
