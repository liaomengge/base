package com.github.liaomengge.base_common.thread.pool.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by liaomengge on 2020/8/31.
 */
@Getter
@AllArgsConstructor
public enum QueueTypeEnum {

    ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue"),
    LINKED_BLOCKING_QUEUE("LinkedBlockingQueue"),
    RESIZABLE_CAPACITY_LINKED_BLOCKING_QUEUE("ResizableCapacityLinkedBlockIngQueue"),
    SYNCHRONOUS_QUEUE("SynchronousQueue"),
    DELAY_QUEUE("DelayQueue"),
    LINKED_BLOCKING_DEQUE("LinkedBlockingDeque"),
    LINKED_TRANSFER_QUEUE("LinkedTransferQueue"),
    PRIORITY_BLOCKING_QUEUE("PriorityBlockingQueue");

    private final String queueType;

    public static QueueTypeEnum matchQueueType(String queueType) {
        return Arrays.stream(QueueTypeEnum.values())
                .filter(val -> StringUtils.equals(val.getQueueType(), queueType))
                .findFirst().orElse(RESIZABLE_CAPACITY_LINKED_BLOCKING_QUEUE);
    }
}
