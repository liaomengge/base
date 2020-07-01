package cn.ly.base_common.metric.metrics.consts;

/**
 * Created by liaomengge on 2019/5/31.
 */
public interface MetricsConst {

    interface ThreadConst {
        String POOL_SIZE_SUFFIX = ".threads.poolSize";//池中当前的线程数
        String ACTIVE_COUNT_SUFFIX = ".threads.activeCount";//正在执行的线程数
        String CORE_POOL_SIZE_SUFFIX = ".threads.corePoolSize";//核心线程数
        String MAX_POOL_SIZE_SUFFIX = ".threads.maxPoolSize";//最大线程数
        String QUEUE_SIZE_SUFFIX = ".threads.queueSize";//队列可接受的元素个数
    }
}
