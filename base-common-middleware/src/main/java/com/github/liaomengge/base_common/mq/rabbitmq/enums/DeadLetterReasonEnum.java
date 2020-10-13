package com.github.liaomengge.base_common.mq.rabbitmq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by liaomengge on 16/12/21.
 */
@AllArgsConstructor
@Getter
public enum DeadLetterReasonEnum {

    REJECTED("rejected", "拒绝"), EXPIRED("expired", "过期"), MAXLEN("maxlen", "超过最大队列长度");

    private String code;
    private String description;
}
