package com.github.liaomengge.base_common.thread.pool;

import com.github.liaomengge.base_common.thread.pool.enums.QueueTypeEnum;
import com.github.liaomengge.base_common.thread.pool.enums.RejectionPolicyEnum;
import com.github.liaomengge.base_common.thread.pool.queue.ResizableCapacityLinkedBlockIngQueue;
import com.github.liaomengge.base_common.utils.thread.LyRuntimeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.*;

/**
 * Created by liaomengge on 2019/5/17.
 */
@Data
@ConfigurationProperties("base.thread-pool")
public class ThreadPoolGroupProperties {

    private List<ThreadPoolProperties> groups = Lists.newArrayList();

    @Data
    public static class ThreadPoolProperties {
        private boolean ttlEnabled = false;
        private boolean requestContextEnabled = false;
        private String threadName;
        private String beanName;
        private int corePoolSize = LyRuntimeUtil.getCpuNum();//可动态修改
        private int maxPoolSize = corePoolSize * 2;//可动态修改
        private int keepAliveSeconds = 30;//可动态修改
        private int queueCapacity = 64;//可动态修改
        private boolean allowCoreThreadTimeOut = false;//可动态修改
        private boolean waitForTasksToCompleteOnShutdown = false;
        private long checkIntervalMillis = 2000L;//默认：check时间间隔2s
        private long awaitTerminationMillis = 0L;
        private String queueType = QueueTypeEnum.RESIZABLE_CAPACITY_LINKED_BLOCKING_QUEUE.getQueueType();
        private String rejectionPolicy = RejectionPolicyEnum.CALLER_RUNS_POLICY.getRejectionPolicy();//可动态修改

        public String buildThreadName() {
            if (StringUtils.isBlank(this.getThreadName())) {
                return "async";
            }
            return this.getThreadName();
        }

        public String buildBeanName() {
            if (StringUtils.isBlank(this.getBeanName())) {
                return this.buildThreadName() + "ThreadPool";
            }
            return this.getBeanName();
        }

        public BlockingQueue buildBlockingQueue() {
            BlockingQueue blockingQueue;
            QueueTypeEnum queueTypeEnum = QueueTypeEnum.matchQueueType(this.queueType);
            switch (queueTypeEnum) {
                case ARRAY_BLOCKING_QUEUE:
                    blockingQueue = new ArrayBlockingQueue(getQueueCapacity());
                    break;
                case LINKED_BLOCKING_QUEUE:
                    blockingQueue = new LinkedBlockingQueue(getQueueCapacity());
                    break;
                case RESIZABLE_CAPACITY_LINKED_BLOCKING_QUEUE:
                    blockingQueue = new ResizableCapacityLinkedBlockIngQueue<>(getQueueCapacity());
                    break;
                case SYNCHRONOUS_QUEUE:
                    blockingQueue = new SynchronousQueue();
                    break;
                case DELAY_QUEUE:
                    blockingQueue = new DelayQueue();
                    break;
                case LINKED_BLOCKING_DEQUE:
                    blockingQueue = new LinkedBlockingDeque(getQueueCapacity());
                    break;
                case LINKED_TRANSFER_QUEUE:
                    blockingQueue = new LinkedTransferQueue();
                    break;
                case PRIORITY_BLOCKING_QUEUE:
                    blockingQueue = new PriorityBlockingQueue(getQueueCapacity());
                    break;
                default:
                    blockingQueue = new ResizableCapacityLinkedBlockIngQueue<>(getQueueCapacity());
                    break;
            }
            return blockingQueue;
        }

        public RejectedExecutionHandler buildRejectionPolicy() {
            RejectionPolicyEnum rejectionPolicyEnum = RejectionPolicyEnum.matchRejectionPolicy(this.rejectionPolicy);
            if (rejectionPolicyEnum == RejectionPolicyEnum.CALLER_RUNS_POLICY) {
                return new ThreadPoolExecutor.CallerRunsPolicy();
            }
            if (rejectionPolicyEnum == RejectionPolicyEnum.DISCARD_POLICY) {
                return new ThreadPoolExecutor.DiscardPolicy();
            }
            if (rejectionPolicyEnum == RejectionPolicyEnum.DISCARD_OLDEST_POLICY) {
                return new ThreadPoolExecutor.DiscardOldestPolicy();
            }
            if (rejectionPolicyEnum == RejectionPolicyEnum.ABORT_POLICY) {
                return new ThreadPoolAbortPolicy(buildBeanName());
            }
            ServiceLoader<RejectedExecutionHandler> serviceLoader = ServiceLoader.load(RejectedExecutionHandler.class);
            Optional<RejectedExecutionHandler> rejectedExecutionHandler = Streams.stream(serviceLoader)
                    .filter(val -> StringUtils.equals(this.rejectionPolicy, val.getClass().getSimpleName())).findFirst();
            return rejectedExecutionHandler.orElseGet(ThreadPoolExecutor.CallerRunsPolicy::new);
        }
    }

    public static class ThreadPoolAbortPolicy extends ThreadPoolExecutor.AbortPolicy {

        private String threadBeanName;

        public ThreadPoolAbortPolicy(String threadBeanName) {
            this.threadBeanName = threadBeanName;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("ThreadPoolName [" + threadBeanName + "], Task " + r.toString() + " " +
                    "rejected from " + e.toString());
        }
    }
}
