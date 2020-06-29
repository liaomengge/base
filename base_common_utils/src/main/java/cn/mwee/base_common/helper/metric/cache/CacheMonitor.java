package cn.mwee.base_common.helper.metric.cache;

import cn.mwee.base_common.helper.cache.CachePool;
import cn.mwee.base_common.helper.metric.AbstractMetricMonitor;
import cn.mwee.base_common.helper.metric.consts.SysMetricsConst;
import lombok.Setter;

/**
 * Created by liaomengge on 16/11/10.
 */
public class CacheMonitor extends AbstractMetricMonitor {

    @Setter
    private CachePool cachePool;

    @Override
    public void execute() {
        final String metricsPrefix = SysMetricsConst.PREFIX_CACHE;
        statsDClient.time(metricsPrefix + prefix + SysMetricsConst.CACHE_MEM_COUNT + suffix, cachePool.size());
    }
}
