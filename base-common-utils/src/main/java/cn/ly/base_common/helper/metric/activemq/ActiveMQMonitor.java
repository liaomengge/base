package cn.ly.base_common.helper.metric.activemq;

import cn.ly.base_common.helper.metric.consts.SysMetricsConst;

import com.timgroup.statsd.StatsDClient;

import lombok.Setter;

/**
 * Created by liaomengge on 17/2/13.
 */
public class ActiveMQMonitor {

    @Setter
    private StatsDClient statsDClient;

    public void monitorCount(String metricsActiveMQ) {
        final String metricsPrefix = SysMetricsConst.PREFIX_ACTIVEMQ;
        statsDClient.increment(metricsPrefix + metricsActiveMQ);
    }

    public void monitorTime(String metricsActiveMQ, long time) {
        final String metricsPrefix = SysMetricsConst.PREFIX_ACTIVEMQ;
        statsDClient.recordExecutionTime(metricsPrefix + metricsActiveMQ, time, 1);
    }

}
