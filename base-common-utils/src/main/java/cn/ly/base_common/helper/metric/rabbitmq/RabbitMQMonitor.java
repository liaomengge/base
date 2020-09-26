package cn.ly.base_common.helper.metric.rabbitmq;

import cn.ly.base_common.helper.metric.consts.SysMetricsConst;

import com.timgroup.statsd.StatsDClient;

import lombok.Setter;

/**
 * Created by liaomengge on 17/2/13.
 */
public class RabbitMQMonitor {

    @Setter
    private StatsDClient statsDClient;

    public void monitorCount(String metricsRabbitmq) {
        final String metricsPrefix = SysMetricsConst.PREFIX_RABBITMQ;
        statsDClient.increment(metricsPrefix + metricsRabbitmq);
    }

    public void monitorTime(String metricsRabbitmq, long time) {
        final String metricsPrefix = SysMetricsConst.PREFIX_RABBITMQ;
        statsDClient.recordExecutionTime(metricsPrefix + metricsRabbitmq, time, 1);
    }

}
