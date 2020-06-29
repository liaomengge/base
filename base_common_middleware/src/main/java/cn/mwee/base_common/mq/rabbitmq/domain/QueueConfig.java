package cn.mwee.base_common.mq.rabbitmq.domain;

import cn.mwee.base_common.mq.consts.MQConst.RabbitMQ;
import cn.mwee.base_common.mq.domain.AbstractQueueConfig;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by liaomengge on 2018/6/29.
 */
@EqualsAndHashCode(callSuper = false)
public class QueueConfig extends AbstractQueueConfig {

    @Getter
    @Setter
    private String exchangeName;

    @Setter
    private String[] queueNames;

    public QueueConfig(String exchangeName, String baseQueueName) {
        super(baseQueueName);
        this.exchangeName = exchangeName;
    }

    public QueueConfig(String exchangeName, String baseQueueName, int queueCount) {
        super(baseQueueName, queueCount);
        this.exchangeName = exchangeName;
    }

    public String buildRouteKey(int hash) {
        return this.buildQueueName(hash) + RabbitMQ.ROUTE_KEY_SUFFIX;
    }

    @Override
    public String[] buildQueueNames() {
        if (ArrayUtils.isEmpty(this.queueNames)) {
            return super.buildQueueNames();
        }
        return this.queueNames;
    }

    @Override
    public String buildQueueName(int hash) {
        if (ArrayUtils.isEmpty(this.queueNames)) {
            return super.buildQueueName(hash);
        }
        return this.queueNames[Math.abs(hash) % this.queueNames.length];
    }
}
