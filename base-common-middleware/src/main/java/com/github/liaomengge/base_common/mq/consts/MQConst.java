package com.github.liaomengge.base_common.mq.consts;

/**
 * Created by liaomengge on 2018/12/4.
 */
public interface MQConst {

    String DEFAULT_CHARSET = "UTF-8";
    long DEFAULT_RECEIVE_TIMEOUT = 5_000L;
    int DEFAULT_QUEUE_COUNT = 1;

    String MQ_TRACE_ID = "MQ_TRACE_ID";
    String MQ_SEND_TIME = "MQ_SEND_TIME";

    interface RabbitMQ {
        String MONITOR_PREFIX = "rabbitmq";
        String ROUTE_KEY_SUFFIX = "_key";
    }

    interface ActiveMQ {
        String MONITOR_PREFIX = "activemq";
        String BACKUP_QUEUE_SUFFIX = "_Bak";
    }
}
