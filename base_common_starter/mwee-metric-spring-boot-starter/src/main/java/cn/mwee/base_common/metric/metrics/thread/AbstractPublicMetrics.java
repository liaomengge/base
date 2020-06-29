package cn.mwee.base_common.metric.metrics.thread;

import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.mwee.base_common.metric.metrics.consts.MetricsConst.ThreadConst.*;

/**
 * Created by liaomengge on 2019/5/31.
 */
public abstract class AbstractPublicMetrics implements PublicMetrics, ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected List<Metric<?>> addMetric(String threadName, ThreadPoolExecutor executor) {
        List<Metric<?>> metrics = Lists.newArrayList();
        metrics.add(new Metric<>(threadName + POOL_SIZE_SUFFIX, executor.getPoolSize()));
        metrics.add(new Metric<>(threadName + ACTIVE_COUNT_SUFFIX, executor.getActiveCount()));
        metrics.add(new Metric<>(threadName + CORE_POOL_SIZE_SUFFIX, executor.getCorePoolSize()));
        metrics.add(new Metric<>(threadName + MAX_POOL_SIZE_SUFFIX, executor.getMaximumPoolSize()));
        metrics.add(new Metric<>(threadName + QUEUE_SIZE_SUFFIX, executor.getQueue().size()));
        return metrics;
    }
}
