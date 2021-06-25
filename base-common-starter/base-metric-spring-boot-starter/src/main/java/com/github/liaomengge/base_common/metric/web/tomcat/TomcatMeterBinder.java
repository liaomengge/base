package com.github.liaomengge.base_common.metric.web.tomcat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.web.tomcat.TomcatMetricsBinder;
import org.springframework.boot.context.event.ApplicationStartedEvent;

/**
 * Created by liaomengge on 2020/9/16.
 */
@Slf4j
public class TomcatMeterBinder extends TomcatMetricsBinder {
    
    public TomcatMeterBinder(MeterRegistry registry) {
        super(registry);
    }

    public TomcatMeterBinder(MeterRegistry registry, Iterable<Tag> tags) {
        super(registry, tags);
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
