package cn.ly.base_common.metric.web.tomcat;

import cn.ly.base_common.utils.log4j2.LyLogger;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import org.slf4j.Logger;
import org.springframework.boot.context.event.ApplicationStartedEvent;

/**
 * Created by liaomengge on 2020/9/16.
 */
public class TomcatMetricsBinder extends org.springframework.boot.actuate.metrics.web.tomcat.TomcatMetricsBinder {

    private static final Logger log = LyLogger.getInstance(TomcatMetricsBinder.class);

    public TomcatMetricsBinder(MeterRegistry meterRegistry) {
        super(meterRegistry);
    }

    public TomcatMetricsBinder(MeterRegistry meterRegistry, Iterable<Tag> tags) {
        super(meterRegistry, tags);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            super.onApplicationEvent(event);
        } catch (Exception e) {
            log.error("metric tomcat error", e);
        }
    }
}
