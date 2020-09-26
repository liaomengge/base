package cn.ly.base_common.helper.metric.jvm;

import cn.ly.base_common.helper.metric.AbstractMonitorConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by liaomengge on 2016/10/29.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JvmMonitorConfig extends AbstractMonitorConfig {

    private double memMaxRatio = 0.95;
}
