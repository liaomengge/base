package cn.ly.base_common.metric.cache.redis;

import cn.ly.base_common.redis.JedisClusterProperties;
import cn.ly.base_common.redis.SpringDataProperties;
import cn.ly.base_common.utils.log4j2.LyLogger;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.ToDoubleFunction;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;

import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by liaomengge on 2020/9/22.
 */
public class RedisCacheMetricsBinder implements MeterBinder {

    private static final Logger log = LyLogger.getInstance(RedisCacheMetricsBinder.class);

    private static final String JMX_NAME = "org.apache.commons.pool2:type=GenericObjectPool,name=";

    private final Iterable<Tag> tags;
    private JedisPoolConfig jedisPoolConfig;
    private JedisClusterProperties jedisClusterProperties;
    private SpringDataProperties springDataProperties;

    private MBeanServer mBeanServer;

    public RedisCacheMetricsBinder(JedisClusterProperties jedisClusterProperties,
                                   SpringDataProperties springDataProperties) {
        this(Collections.emptyList(), jedisClusterProperties, springDataProperties);
    }

    public RedisCacheMetricsBinder(Iterable<Tag> tags, JedisClusterProperties jedisClusterProperties,
                                   SpringDataProperties springDataProperties) {
        this.tags = tags;
        this.jedisClusterProperties = jedisClusterProperties;
        this.springDataProperties = springDataProperties;
    }

    public RedisCacheMetricsBinder(JedisPoolConfig jedisPoolConfig) {
        this(Collections.emptyList(), jedisPoolConfig);
    }

    public RedisCacheMetricsBinder(Iterable<Tag> tags, JedisPoolConfig jedisPoolConfig) {
        this.tags = tags;
        this.jedisPoolConfig = jedisPoolConfig;
    }

    public static void monitor(MeterRegistry registry, JedisPoolConfig jedisPoolConfig) {
        monitor(registry, Collections.emptyList(), jedisPoolConfig);
    }

    public static void monitor(MeterRegistry registry, Iterable<Tag> tags, JedisPoolConfig jedisPoolConfig) {
        new RedisCacheMetricsBinder(tags, jedisPoolConfig).bindTo(registry);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        try {
            registerMetrics(registry);
        } catch (Exception e) {
            log.error("metric redis cache error", e);
        }
    }

    private void registerMetrics(MeterRegistry registry) throws Exception {
        if (Objects.nonNull(this.mBeanServer)) {
            if (Objects.isNull(jedisClusterProperties) && Objects.isNull(springDataProperties) && Objects.isNull(jedisPoolConfig)) {
                return;
            }
            if (Objects.nonNull(jedisClusterProperties) && jedisClusterProperties.getPool().getJmxEnabled()) {
                String jmxNamePrefix = jedisClusterProperties.getPool().getJmxNamePrefix();
                statsMaxPool(jmxNamePrefix, registry);
            }
            if (Objects.nonNull(springDataProperties) && springDataProperties.getPool().getJmxEnabled()) {
                String jmxNamePrefix = springDataProperties.getPool().getJmxNamePrefix();
                statsMaxPool(jmxNamePrefix, registry);
            }
            if (Objects.nonNull(jedisPoolConfig) && jedisPoolConfig.getJmxEnabled()) {
                String jmxNamePrefix = jedisPoolConfig.getJmxNamePrefix();
                statsMaxPool(jmxNamePrefix, registry);
            }
        }
    }

    private void statsMaxPool(String jmxNamePrefix, MeterRegistry registry) throws Exception {
        ObjectName jmxObjectName = new ObjectName(JMX_NAME + jmxNamePrefix + "*");
        Set<ObjectName> objectNames = this.mBeanServer.queryNames(jmxObjectName, null);
        if (CollectionUtils.isNotEmpty(objectNames)) {
            ObjectName maxObjectBean =
                    objectNames.stream().filter(Objects::nonNull).max(Comparator.comparingDouble(val2 -> safeDouble(() -> mBeanServer.getAttribute(val2,
                            "NumActive")))).orElse(null);
            Optional.ofNullable(maxObjectBean).ifPresent(val -> statsPool(registry, mBeanServer, val));
        }
    }

    private void statsPool(MeterRegistry registry, MBeanServer mBeanServer, ObjectName objectName) {
        bindCounter(registry, "redis.max.idle", mBeanServer, objectName,
                toDoubleFunction(mBeanServer, objectName, "MaxIdle"));
        bindCounter(registry, "redis.min.idle", mBeanServer, objectName,
                toDoubleFunction(mBeanServer, objectName, "MinIdle"));
        bindCounter(registry, "redis.num.active", mBeanServer, objectName,
                toDoubleFunction(mBeanServer, objectName, "NumActive"));
        bindCounter(registry, "redis.num.idle", mBeanServer, objectName,
                toDoubleFunction(mBeanServer, objectName, "NumIdle"));
        bindCounter(registry, "redis.max.total", mBeanServer, objectName,
                toDoubleFunction(mBeanServer, objectName, "MaxTotal"));
        bindCounter(registry, "redis.num.waiters", mBeanServer, objectName,
                toDoubleFunction(mBeanServer, objectName, "NumWaiters"));
    }

    public void bindCounter(MeterRegistry registry, String name, MBeanServer mBeanServer, ObjectName objectName,
                            ToDoubleFunction<MBeanServer> function) {
        FunctionCounter.builder(name, mBeanServer, function)
                .tags(Tags.concat(tags, "redis.jmx.name", objectName.getKeyProperty("name"))).register(registry);
    }

    private ToDoubleFunction<MBeanServer> toDoubleFunction(MBeanServer mBeanServer, ObjectName objectName,
                                                           String attribute) {
        return val -> safeDouble(() -> mBeanServer.getAttribute(objectName, attribute));
    }

    private double safeDouble(Callable<Object> callable) {
        try {
            return Double.parseDouble(callable.call().toString());
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    @PostConstruct
    public void init() {
        this.mBeanServer = this.getMBeanServer();
    }

    private MBeanServer getMBeanServer() {
        List<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (!mBeanServers.isEmpty()) {
            return mBeanServers.get(0);
        }
        return ManagementFactory.getPlatformMBeanServer();
    }
}
