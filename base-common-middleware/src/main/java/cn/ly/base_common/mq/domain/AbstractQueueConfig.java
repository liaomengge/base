package cn.ly.base_common.mq.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import static cn.ly.base_common.mq.consts.MQConst.DEFAULT_QUEUE_COUNT;

/**
 * Created by liaomengge on 2018/7/20.
 */
@Data
@Getter
@Setter
public abstract class AbstractQueueConfig {

    protected String baseQueueName;
    protected int queueCount = DEFAULT_QUEUE_COUNT;

    public AbstractQueueConfig(String baseQueueName) {
        this.baseQueueName = baseQueueName;
    }

    public AbstractQueueConfig(String baseQueueName, int queueCount) {
        this.baseQueueName = baseQueueName;
        this.queueCount = queueCount;
    }

    public String[] buildQueueNames() {
        String[] queueNames = new String[queueCount];
        for (int i = 0; i < queueCount; i++) {
            queueNames[i] = this.buildQueueName(i);
        }
        return queueNames;
    }

    public String buildQueueName(int hash) {
        String baseQueueName = this.getBaseQueueName();
        int queueCount = this.getQueueCount();
        int modHash = Math.abs(hash) % queueCount;
        if (modHash == 0) {
            return baseQueueName;
        }
        return baseQueueName + "_" + modHash;
    }
}
