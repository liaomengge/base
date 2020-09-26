package cn.ly.base_common.helper.metric.thread;

import cn.ly.base_common.helper.metric.AbstractMonitorConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by liaomengge on 16/11/10.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ThreadMonitorConfig extends AbstractMonitorConfig {

    private int threadMaxCount = 1000;
}
