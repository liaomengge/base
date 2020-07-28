package cn.ly.base_common.influx.consts;

/**
 * Created by liaomengge on 2020/7/21.
 */
public interface InfluxConst {

    String DEFAULT_DATABASE = "WatchDB";
    String DEFAULT_MEASUREMENT = "WATCH";

    /**
     * influx batch properties
     */
    int DEFAULT_INFLUX_BATCH_ACTIONS_LIMIT = 1000;
    int DEFAULT_INFLUX_BATCH_INTERVAL_DURATION = 100;

    /**
     * local block queue batch properties
     */
    int DEFAULT_BLOCKING_QUEUE_SIZE = 5000;
    int DEFAULT_TAKE_BATCH_SIZE = 1000;
    int DEFAULT_TAKE_BATCH_TIMEOUT = 100;

    /**
     * 兼容老版本
     */
    String OLD_EVENT_WATCH_NAME = "EventWatchName";
    String OLD_APPID_WATCH_NAME = "AppIdWatchName";
    String OLD_WATCH_VALUE = "WatchValue";
}
