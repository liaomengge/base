package cn.ly.base_common.mq.rabbitmq.monitor;

import cn.ly.base_common.mq.consts.MQConst;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Setter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 17/2/13.
 */
public class DefaultMQMonitor {

    @Setter
    private MeterRegistry meterRegistry;

    public void monitorCount(String metricsRabbitmq) {
        final String metricsPrefix = MQConst.RabbitMQ.MONITOR_PREFIX;
        Optional.ofNullable(meterRegistry).ifPresent(val -> val.counter(metricsPrefix + metricsRabbitmq).increment());
    }

    public void monitorTime(String metricsActiveMQ, long elapsedNanoTime) {
        final String metricsPrefix = MQConst.RabbitMQ.MONITOR_PREFIX;
        Optional.ofNullable(meterRegistry).ifPresent(val -> val.timer(metricsPrefix + metricsActiveMQ).record(elapsedNanoTime, TimeUnit.NANOSECONDS));
    }

}
