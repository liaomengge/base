package cn.ly.base_common.helper.concurrent;

import cn.ly.base_common.utils.thread.LyThreadFactoryBuilderUtil;
import cn.ly.base_common.utils.thread.LyThreadPoolExecutorUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.*;

/**
 * Created by liaomengge on 2019/2/22.
 */
@Getter
@Setter
public class LyThreadPoolExecutor extends ThreadPoolExecutor implements InitializingBean, DisposableBean {

    private String threadName;

    private boolean waitForTasksToCompleteOnShutdown = false;

    private int awaitTerminationSeconds = 0;

    private int checkInterval = 2;//默认：check时间间隔2s

    public LyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public LyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public LyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public LyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void shutdown() {
        if (!this.waitForTasksToCompleteOnShutdown || this.awaitTerminationSeconds <= 0) {
            super.shutdown();
            return;
        }
        LyThreadPoolExecutorUtil.registerShutdownHook(this, awaitTerminationSeconds, this.checkInterval);
    }

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isNotBlank(threadName)) {
            super.setThreadFactory(LyThreadFactoryBuilderUtil.build(threadName));
        }
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }
}
