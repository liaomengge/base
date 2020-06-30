package cn.ly.base_common.metric.metrics.thread.custom;

import cn.ly.base_common.metric.metrics.thread.AbstractPublicMetrics;
import cn.mwee.base_common.helper.concurrent.MwThreadPoolTaskExecutor;
import cn.mwee.base_common.helper.concurrent.MwThreadPoolTaskWrappedExecutor;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2019/5/31.
 */
public class MwThreadPoolPublicMetrics extends AbstractPublicMetrics {

    @Override
    public Collection<Metric<?>> metrics() {
        Map<String, MwThreadPoolTaskWrappedExecutor> executorMap =
                this.applicationContext.getBeansOfType(MwThreadPoolTaskWrappedExecutor.class);
        if (MapUtils.isNotEmpty(executorMap)) {
            return executorMap.values().parallelStream().map(wrappedExecutor -> {
                ThreadPoolTaskExecutor threadPoolTaskExecutor = wrappedExecutor.getThreadPoolTaskExecutor();
                List<Metric<?>> metrics = Lists.newArrayList();
                if (threadPoolTaskExecutor instanceof MwThreadPoolTaskExecutor) {
                    MwThreadPoolTaskExecutor mwThreadPoolTaskExecutor =
                            (MwThreadPoolTaskExecutor) threadPoolTaskExecutor;
                    ThreadPoolExecutor executor = mwThreadPoolTaskExecutor.getThreadPoolExecutor();
                    String threadName = mwThreadPoolTaskExecutor.getThreadName();
                    metrics = addMetric(threadName, executor);
                }
                return metrics;
            }).flatMap(Collection::stream).collect(Collectors.toList());
        }
        return Collections.emptySet();
    }
}
