package cn.ly.base_common.thread.pool;

import cn.ly.base_common.support.extension.ExtensionLoader;
import cn.ly.base_common.thread.pool.queue.ResizableCapacityLinkedBlockIngQueue;
import cn.ly.base_common.utils.thread.LyRuntimeUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.*;

import static cn.ly.base_common.thread.pool.ThreadPoolGroupProperties.QueueTypeEnum.RESIZABLE_CAPACITY_LINKED_BLOCKING_QUEUE;
import static cn.ly.base_common.thread.pool.ThreadPoolGroupProperties.RejectionPolicyEnum.CALLER_RUNS;

/**
 * Created by liaomengge on 2019/5/17.
 */
@Data
@ConfigurationProperties(prefix = "ly.thread-pool")
public class ThreadPoolGroupProperties {

    private List<ThreadPoolProperties> groups = Lists.newArrayList();

    @Data
    public static class ThreadPoolProperties {
        private boolean ttlEnabled = false;
        private String threadName;
        private String beanName;
        private int corePoolSize = LyRuntimeUtil.getCpuNum();
        private int maxPoolSize = corePoolSize * 2;
        private int keepAliveSeconds = 30;
        private int queueCapacity = 64;
        private boolean allowCoreThreadTimeOut = false;
        private boolean waitForTasksToCompleteOnShutdown = false;
        private int checkInterval = 2;//默认：check时间间隔2s
        private int awaitTerminationSeconds = 0;
        private QueueTypeEnum queueType = RESIZABLE_CAPACITY_LINKED_BLOCKING_QUEUE;
        private RejectionPolicyEnum rejectionPolicy = CALLER_RUNS;

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
            switch (this.queueType) {
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
                case PRIORITY_BLOCKING_QUEUE:
                    blockingQueue = new LinkedTransferQueue();
                    break;
                default:
                    blockingQueue = new ResizableCapacityLinkedBlockIngQueue<>(getQueueCapacity());
                    break;
            }
            return blockingQueue;
        }

        public RejectedExecutionHandler buildRejectionPolicy() {
            RejectedExecutionHandler rejectedExecutionHandler;
            switch (this.rejectionPolicy) {
                case CALLER_RUNS:
                    rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                    break;
                case DISCARD:
                    rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                    break;
                case DISCARD_OLDEST:
                    rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
                    break;
                case ABORT:
                    rejectedExecutionHandler = new ThreadPoolAbortPolicy(buildBeanName());
                    break;
                case CUSTOM:
                    ExtensionLoader<RejectedExecutionHandler> loader =
                            ExtensionLoader.getLoader(RejectedExecutionHandler.class);
                    rejectedExecutionHandler = loader.getExtension(buildBeanName());
                    break;
                default:
                    rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                    break;
            }
            return rejectedExecutionHandler;
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

    public enum RejectionPolicyEnum {
        ABORT, CALLER_RUNS, DISCARD, DISCARD_OLDEST, CUSTOM
    }

    public enum QueueTypeEnum {
        ARRAY_BLOCKING_QUEUE, LINKED_BLOCKING_QUEUE, RESIZABLE_CAPACITY_LINKED_BLOCKING_QUEUE,
        SYNCHRONOUS_QUEUE, DELAY_QUEUE, LINKED_BLOCKING_DEQUE, PRIORITY_BLOCKING_QUEUE
    }
}
