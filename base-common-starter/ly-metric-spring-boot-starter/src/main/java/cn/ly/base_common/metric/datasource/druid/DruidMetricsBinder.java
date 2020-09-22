package cn.ly.base_common.metric.datasource.druid;

import cn.ly.base_common.utils.log4j2.LyLogger;
import com.alibaba.druid.pool.DruidDataSource;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.slf4j.Logger;
import org.springframework.boot.jdbc.DataSourceUnwrapper;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static cn.ly.base_common.metric.consts.MetricsConst.DRUID_PREFIX;

/**
 * Created by liaomengge on 2020/9/17.
 */
public class DruidMetricsBinder implements MeterBinder {

    private static final Logger log = LyLogger.getInstance(DruidMetricsBinder.class);

    private final Iterable<Tag> tags;
    private final List<DataSource> dataSources;

    public DruidMetricsBinder(List<DataSource> dataSources) {
        this(Collections.emptyList(), dataSources);
    }

    public DruidMetricsBinder(Iterable<Tag> tags, List<DataSource> dataSources) {
        this.tags = tags;
        this.dataSources = dataSources;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        try {
            if (Objects.nonNull(dataSources)) {
                List<DruidDataSource> druidDataSources =
                        dataSources.stream().map(dataSource -> DataSourceUnwrapper.unwrap(dataSource,
                                DruidDataSource.class))
                                .filter(Objects::nonNull).collect(Collectors.toList());
                registerMetrics(registry, druidDataSources);
            }
        } catch (Exception e) {
            log.error("metric druid error", e);
        }
    }

    private void registerMetrics(MeterRegistry registry, List<DruidDataSource> druidDataSources) {
        druidDataSources.forEach(druidDataSource -> {
            //最大等待时间
            bindTimeGauge(registry, DRUID_PREFIX + "max.wait.millis", druidDataSource, DruidDataSource::getMaxWait);
            //当前等待获取连接的线程数
            bindGauge(registry, DRUID_PREFIX + "wait.thread.count", druidDataSource,
                    DruidDataSource::getWaitThreadCount);
            //最大等待线程数
            bindGauge(registry, DRUID_PREFIX + "max.wait.thread.count", druidDataSource,
                    DruidDataSource::getMaxWaitThreadCount);
            //获取连接时累计等待多长时间
            bindTimeGauge(registry, DRUID_PREFIX + "not.empty.wait.millis", druidDataSource,
                    DruidDataSource::getNotEmptyWaitMillis);
            //获取连接时累计等待多少次
            bindGauge(registry, DRUID_PREFIX + "not.empty.wait.count", druidDataSource,
                    DruidDataSource::getNotEmptyWaitCount);

            //最小空闲数
            bindGauge(registry, DRUID_PREFIX + "min.idle", druidDataSource, DruidDataSource::getMinIdle);
            //最大空闲数
            bindGauge(registry, DRUID_PREFIX + "max.idle", druidDataSource, DruidDataSource::getMaxIdle);
            //最大活跃数
            bindGauge(registry, DRUID_PREFIX + "max.active", druidDataSource, DruidDataSource::getMaxActive);

            //当前连接池数
            bindGauge(registry, DRUID_PREFIX + "pooling.count", druidDataSource, DruidDataSource::getPoolingCount);
            //连接池峰值
            bindGauge(registry, DRUID_PREFIX + "pooling.peak", druidDataSource, DruidDataSource::getPoolingPeak);
            //当前活跃连接数
            bindGauge(registry, DRUID_PREFIX + "active.count", druidDataSource, DruidDataSource::getActiveCount);
            //活跃数峰值
            bindGauge(registry, DRUID_PREFIX + "active.peak", druidDataSource, DruidDataSource::getActivePeak);

            //执行数
            bindCounter(registry, DRUID_PREFIX + "execute.count", druidDataSource, DruidDataSource::getExecuteCount);
            //错误数
            bindCounter(registry, DRUID_PREFIX + "error.count", druidDataSource, DruidDataSource::getErrorCount);
            //回滚数
            bindCounter(registry, DRUID_PREFIX + "rollback.count", druidDataSource, DruidDataSource::getRollbackCount);
        });
    }

    private void bindGauge(MeterRegistry registry, String name, DruidDataSource druidDataSource,
                           ToDoubleFunction<DruidDataSource> function) {
        Gauge.builder(name, druidDataSource, function)
                .tags(Tags.concat(tags, "pool.name", druidDataSource.getName())).register(registry);
    }

    private void bindTimeGauge(MeterRegistry registry, String name, DruidDataSource druidDataSource,
                               ToDoubleFunction<DruidDataSource> function) {
        TimeGauge.builder(name, druidDataSource, TimeUnit.MILLISECONDS, function)
                .tags(Tags.concat(tags, "pool.name", druidDataSource.getName())).register(registry);
    }

    private void bindCounter(MeterRegistry registry, String name, DruidDataSource druidDataSource,
                             ToDoubleFunction<DruidDataSource> function) {
        FunctionCounter.builder(name, druidDataSource, function)
                .tags(Tags.concat(tags, "pool.name", druidDataSource.getName())).register(registry);
    }
}
