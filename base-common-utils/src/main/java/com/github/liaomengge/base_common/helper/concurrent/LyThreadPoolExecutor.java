package com.github.liaomengge.base_common.helper.concurrent;

import com.github.liaomengge.base_common.utils.thread.LyThreadFactoryBuilderUtil;

import java.util.concurrent.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by liaomengge on 2019/2/22.
 */
@Getter
@Setter
public class LyThreadPoolExecutor extends ThreadPoolExecutor implements InitializingBean, DisposableBean {

    private String threadName;

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
    public void afterPropertiesSet() {
        if (StringUtils.isNotBlank(threadName)) {
            super.setThreadFactory(LyThreadFactoryBuilderUtil.build(threadName));
        }
    }

    @Override
    public void destroy() throws Exception {
        super.shutdown();
    }
}
