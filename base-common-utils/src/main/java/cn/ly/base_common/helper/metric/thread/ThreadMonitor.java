package cn.ly.base_common.helper.metric.thread;

import cn.ly.base_common.helper.metric.AbstractMetricMonitor;
import cn.ly.base_common.helper.metric.consts.SysMetricsConst;

/**
 * Created by liaomengge on 16/11/10.
 */
public class ThreadMonitor extends AbstractMetricMonitor {

    @Override
    public void execute() {
        final String metricsPrefix = SysMetricsConst.PREFIX_THREAD;
        int threadCount = this.getAllThreads();

        statsDClient.time(metricsPrefix + prefix + SysMetricsConst.THREAD_COUNT + suffix, threadCount);
    }

    public int getAllThreads() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();

        while (threadGroup.getParent() != null) {
            threadGroup = threadGroup.getParent();
        }

        return threadGroup.activeCount();
    }
}
