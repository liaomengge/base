package cn.mwee.base_common.thread.pool;

import cn.mwee.base_common.utils.thread.MwRuntimeUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.mwee.base_common.thread.pool.ThreadPoolGroupProperties.RejectionPolicy.CALLER_RUNS;

/**
 * Created by liaomengge on 2019/5/17.
 */
@Data
@ConfigurationProperties(prefix = "mwee.thread-pool")
public class ThreadPoolGroupProperties {

    private List<ThreadPoolProperties> groups = Lists.newArrayList();

    @Data
    public static class ThreadPoolProperties {
        private boolean ttlEnabled = false;
        private String threadName;
        private String beanName;
        private int corePoolSize = MwRuntimeUtil.getCpuNum();
        private int maxPoolSize = corePoolSize * 2;
        private int keepAliveSeconds = 30;
        private int queueCapacity = 64;
        private boolean allowCoreThreadTimeOut = false;
        private boolean waitForTasksToCompleteOnShutdown = false;
        private int checkInterval = 2;//默认：check时间间隔2s
        private int awaitTerminationSeconds = 0;
        private RejectionPolicy rejectionPolicy = CALLER_RUNS;

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

        public RejectedExecutionHandler buildRejectionPolicy() {
            RejectedExecutionHandler rejectedExecutionHandler;
            switch (this.rejectionPolicy) {
                case ABORT:
                    rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
                    break;
                case CALLER_RUNS:
                    rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                    break;
                case DISCARD:
                    rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                    break;
                case DISCARD_OLDEST:
                    rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
                    break;
                default:
                    rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                    break;
            }
            return rejectedExecutionHandler;
        }
    }

    public enum RejectionPolicy {
        ABORT, CALLER_RUNS, DISCARD, DISCARD_OLDEST
    }
}
