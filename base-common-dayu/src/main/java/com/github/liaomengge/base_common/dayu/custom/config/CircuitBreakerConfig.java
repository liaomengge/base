package com.github.liaomengge.base_common.dayu.custom.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by liaomengge on 17/1/6.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CircuitBreakerConfig {

    private int failureIntervalSeconds = 20;//请求处理失败的时间区间, 单位:秒

    private int failureThreshold = 100;//20秒内100次请求失败

    private long resetMilliSeconds;//间隔时间窗, 单位:毫秒
}
