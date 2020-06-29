package cn.mwee.base_common.mq.consts;

/**
 * Created by liaomengge on 2018/12/4.
 */
public final class MQConst {

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final long DEFAULT_RECEIVE_TIMEOUT = 5_000L;
    public static final int DEFAULT_QUEUE_COUNT = 1;

    public static final String MQ_TRACE_ID = "MQ_TRACE_ID";
    public static final String MQ_SEND_TIME = "MQ_SEND_TIME";

    public class RabbitMQ {
        public static final String ROUTE_KEY_SUFFIX = "_key";
    }

    public class ActiveMQ {
        public static final String BACKUP_QUEUE_SUFFIX = "_Bak";
    }
}
