package cn.ly.base_common.helper.metric.jvm;

import cn.ly.base_common.helper.metric.AbstractMetricMonitor;
import cn.ly.base_common.utils.number.LyNumberUtil;
import cn.ly.base_common.helper.metric.consts.SysMetricsConst;
import lombok.Setter;

/**
 * Created by liaomengge on 2016/10/27.
 */
public class JvmMonitor extends AbstractMetricMonitor {

    @Setter
    private JvmMonitorConfig config;

    @Override
    public void execute() {
        double x = 1024 * 1024D;
        Runtime runtime = Runtime.getRuntime();
        double totalMem = runtime.totalMemory() / x;
        double freeMem = runtime.freeMemory() / x;
        double usedMem = totalMem - freeMem;
        double memUsedRatio = usedMem / totalMem;
        memUsedRatio = LyNumberUtil.round(memUsedRatio, 2);

        String metricsPrefix = SysMetricsConst.PREFIX_JVM;
        statsDClient.time(metricsPrefix + prefix + SysMetricsConst.JVM_MEM_USED + suffix, (long) usedMem);
        statsDClient.time(metricsPrefix + prefix + SysMetricsConst.JVM_MEM_TOTAL + suffix, (long) totalMem);

        if (memUsedRatio >= config.getMemMaxRatio()) {
            String log = "当前内存使用率" + memUsedRatio + "达到阈值" + config.getMemMaxRatio() + "!";
            log.warn(log);
        }
    }

}