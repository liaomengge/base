package cn.ly.base_common.metric.cache.local;

import cn.ly.base_common.cache.CachePoolHelper;
import cn.ly.base_common.cache.caffeine.CaffeineCache;
import cn.ly.base_common.utils.log4j2.LyLogger;
import com.github.benmanes.caffeine.cache.Cache;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.slf4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
import java.util.Map;

/**
 * Created by liaomengge on 2020/9/22.
 */
public class LocalCacheMeterBinder implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LyLogger.getInstance(LocalCacheMeterBinder.class);

    private final Iterable<Tag> tags;

    public LocalCacheMeterBinder() {
        this(Collections.emptyList());
    }

    public LocalCacheMeterBinder(Iterable<Tag> tags) {
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
        MeterRegistry registry = context.getBean(MeterRegistry.class);
        CachePoolHelper cachePoolHelper = context.getBean(CachePoolHelper.class);
        Map<String, CaffeineCache> caffeineCacheConcurrentMap = cachePoolHelper.getLevel1CacheMap();
        caffeineCacheConcurrentMap.forEach((region, caffeineCache) -> CaffeineCacheMetrics.monitor(registry,
                caffeineCache.getCache(), region, this.tags));
    }
}
