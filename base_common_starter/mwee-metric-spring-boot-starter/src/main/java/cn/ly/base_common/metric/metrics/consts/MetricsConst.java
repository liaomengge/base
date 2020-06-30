package cn.ly.base_common.metric.metrics.consts;

/**
 * Created by liaomengge on 2019/5/31.
 */
public class MetricsConst {

    public static class ThreadConst {
        public static final String POOL_SIZE_SUFFIX = ".threads.poolSize";//池中当前的线程数
        public static final String ACTIVE_COUNT_SUFFIX = ".threads.activeCount";//正在执行的线程数
        public static final String CORE_POOL_SIZE_SUFFIX = ".threads.corePoolSize";//核心线程数
        public static final String MAX_POOL_SIZE_SUFFIX = ".threads.maxPoolSize";//最大线程数
        public static final String QUEUE_SIZE_SUFFIX = ".threads.queueSize";//队列可接受的元素个数
    }
}
