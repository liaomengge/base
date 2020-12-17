package com.github.liaomengge.base_common.influx.consts;

import java.time.Duration;

/**
 * Created by liaomengge on 2020/7/21.
 */
public interface InfluxConst {

    String DEFAULT_DATABASE = "InfluxDatabase";
    String DEFAULT_MEASUREMENT = "InfluxMeasurement";

    String DEFAULT_INFLUX_POLICY = "autogen";
    String DEFAULT_CONSISTENCY_LEVEL = "ALL";

    String DEFAULT_INSTANCE = "Instance";

    /**
     * influx batch properties
     */
    int DEFAULT_BATCH_ACTIONS_LIMIT = 1000;
    int DEFAULT_BATCH_INTERVAL_DURATION = 1000;
    int DEFAULT_JITTER_INTERVAL_DURATION = 5;
    int DEFAULT_BUFFER_LIMIT = 10000;

    /**
     * local block queue batch properties
     */
    int DEFAULT_QUEUE_CAPACITY = 5000;
    int DEFAULT_TAKE_BATCH_SIZE = 1000;
    int DEFAULT_TAKE_BATCH_TIMEOUT = 100;

    /**
     * okhttp timeout
     */
    int DEFAULT_MAX_CONNECTIONS = 50;
    Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);
    Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(5);
    Duration DEFAULT_WRITE_TIMEOUT = Duration.ofSeconds(5);
}
