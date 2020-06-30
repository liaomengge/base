package cn.ly.base_common.cache.task;

import cn.mwee.base_common.cache.CachePoolHelper;
import cn.mwee.base_common.cache.caffeine.CaffeineCache;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import cn.mwee.base_common.utils.shutdown.MwShutdownUtil;
import cn.mwee.base_common.utils.thread.MwRuntimeUtil;
import cn.mwee.base_common.utils.thread.MwThreadFactoryBuilderUtil;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/7/2.
 */
@Data
@AllArgsConstructor
public class CacheScheduledTask {

    private static final Logger logger = MwLogger.getInstance(CacheScheduledTask.class);

    private static final String METRIC_PREFIX = "metric.";

    private final StatsDClient statsDClient;
    private final CachePoolHelper cachePoolHelper;

    @PostConstruct
    private void init() {
        ScheduledThreadPoolExecutor poolExecutor =
                new ScheduledThreadPoolExecutor(MwRuntimeUtil.getCpuNum(),
                        MwThreadFactoryBuilderUtil.build("cache"), new ThreadPoolExecutor.CallerRunsPolicy());
        poolExecutor.scheduleAtFixedRate(this::metric, 60, 10, TimeUnit.SECONDS);
        this.registerShutdownHook(poolExecutor);
    }

    private void metric() {
        Map<String, CaffeineCache> caffeineCacheMap = cachePoolHelper.getLevel1CacheMap();
        caffeineCacheMap.forEach((key, value) -> {
            String hitRatePrefix = METRIC_PREFIX + "cache." + "hitRate." + key;
            String sizePrefix = METRIC_PREFIX + "cache." + "size." + key;
            statsDClient.recordExecutionTime(hitRatePrefix,
                    BigDecimal.valueOf(value.stats().hitRate()).multiply(BigDecimal.valueOf(100)).longValue());
            statsDClient.recordExecutionTime(sizePrefix, value.size());
        });
    }

    /**
     * 程序退出时的回调勾子
     *
     * @param poolExecutor
     */
    protected void registerShutdownHook(ScheduledThreadPoolExecutor poolExecutor) {
        MwShutdownUtil.registerShutdownHook(() -> {
            try {
                logger.info("Metric Cache Scheduled Thread Pool Exist...");
            } finally {
                if (poolExecutor != null) {
                    poolExecutor.shutdown();
                }
            }
        });
    }
}
