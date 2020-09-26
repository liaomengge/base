package cn.ly.base_common.metric.cache.local;

import cn.ly.base_common.cache.caffeine.CaffeineCache;
import cn.ly.base_common.cache.caffeine.CaffeineCacheManager;
import cn.ly.base_common.utils.log4j2.LyLogger;

import com.github.benmanes.caffeine.cache.Cache;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;

import java.util.Collections;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by liaomengge on 2020/9/22.
 */
public class LocalCacheMetricsBinder implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LyLogger.getInstance(LocalCacheMetricsBinder.class);

    private final Iterable<Tag> tags;

    public LocalCacheMetricsBinder() {
        this(Collections.emptyList());
    }

    public LocalCacheMetricsBinder(Iterable<Tag> tags) {
        this.tags = tags;
    }

    public static <C extends Cache<?, ?>> C monitor(MeterRegistry registry, C cache, String cacheName,
                                                    Iterable<Tag> tags) {
        return CaffeineCacheMetrics.monitor(registry, cache, cacheName, tags);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            registerMetrics(event.getApplicationContext());
        } catch (Exception e) {
            log.error("metric local cache error", e);
        }
    }

    private void registerMetrics(ConfigurableApplicationContext context) {
        MeterRegistry meterRegistry = context.getBean(MeterRegistry.class);
        CaffeineCacheManager caffeineCacheManager = context.getBean(CaffeineCacheManager.class);
        ConcurrentMap<String, CaffeineCache> caffeineCacheConcurrentMap = caffeineCacheManager.getCaffeineCacheMap();
        caffeineCacheConcurrentMap.forEach((region, caffeineCache) -> CaffeineCacheMetrics.monitor(meterRegistry,
                caffeineCache.getCache(), region, this.tags));
    }
}
