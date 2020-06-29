package cn.mwee.base_common.mq.activemq.domain;

import cn.mwee.base_common.mq.domain.AbstractQueueConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by liaomengge on 2018/7/20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class QueueConfig extends AbstractQueueConfig {

    public QueueConfig(String baseQueueName) {
        super(baseQueueName);
    }

    public QueueConfig(String baseQueueName, int queueCount) {
        super(baseQueueName, queueCount);
    }
}
