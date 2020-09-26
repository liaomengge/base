package cn.ly.base_common.metric.redis.task;

/**
 * Created by liaomengge on 2019/7/30.
 */

import cn.ly.base_common.metric.redis.MetricRedisProperties;
import cn.ly.base_common.redis.JedisClusterProperties;
import cn.ly.base_common.redis.SpringDataProperties;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.thread.LyThreadUtil;

import com.google.common.collect.Lists;
import com.timgroup.statsd.StatsDClient;

import java.lang.management.ManagementFactory;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.management.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import lombok.Builder;
import lombok.Data;

public class MetricRedisScheduledTask {

    private static final Logger log = LyLogger.getInstance(MetricRedisScheduledTask.class);

    private static final String METRIC_REDIS_PREFIX = "metric-redis.";
    private static final String JMX_NAME_BASE = "org.apache.commons.pool2:type=GenericObjectPool,name=";

    private StatsDClient statsDClient;
    private MetricRedisProperties metricRedisProperties;
    private JedisClusterProperties jedisClusterProperties;
    private SpringDataProperties springDataProperties;

    private MBeanServer mbeanServer;

    public MetricRedisScheduledTask(StatsDClient statsDClient, MetricRedisProperties metricRedisProperties,
                                    JedisClusterProperties jedisClusterProperties,
                                    SpringDataProperties springDataProperties) {
        this.statsDClient = statsDClient;
        this.metricRedisProperties = metricRedisProperties;
        this.jedisClusterProperties = jedisClusterProperties;
        this.springDataProperties = springDataProperties;
    }

    @PostConstruct
    private void init() {
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        RedisStatsThread redisStatsThread = new RedisStatsThread("metric-redis-stats");
        redisStatsThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(redisStatsThread::interrupt));
    }

    private class RedisStatsThread extends Thread {

        public RedisStatsThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            long initialDelay = metricRedisProperties.getInitialDelay() * 1000L;
            if (initialDelay > 0) {
                LyThreadUtil.sleep(initialDelay);
            }
            while (!this.isInterrupted()) {
                try {
                    if (Objects.nonNull(mbeanServer)) {
                        if (Objects.isNull(jedisClusterProperties) && Objects.isNull(springDataProperties)) {
                            continue;
                        }
                        if (Objects.nonNull(jedisClusterProperties) && Objects.nonNull(springDataProperties)) {
                            String jmxNamePrefix = jedisClusterProperties.getPool().getJmxNamePrefix();
                            String jmxNamePrefix2 = springDataProperties.getPool().getJmxNamePrefix();
                            if (StringUtils.equalsIgnoreCase(jmxNamePrefix, jmxNamePrefix2)) {
                                statsMaxPool(jmxNamePrefix);
                            } else {
                                statsMaxPool(jmxNamePrefix);
                                statsMaxPool(jmxNamePrefix2);
                            }
                        }
                        if (Objects.nonNull(jedisClusterProperties)) {
                            String jmxNamePrefix = jedisClusterProperties.getPool().getJmxNamePrefix();
                            statsMaxPool(jmxNamePrefix);
                        } else {
                            String jmxNamePrefix2 = springDataProperties.getPool().getJmxNamePrefix();
                            statsMaxPool(jmxNamePrefix2);
                        }
                    }
                    TimeUnit.SECONDS.sleep(metricRedisProperties.getStatsInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("metric redis interrupt exit...");
                } catch (Exception e) {
                    log.error("metric redis exception...", e);
                }
            }
        }
    }

    private void statsMaxPool(String jmxNamePrefix) throws Exception {
        List<PoolStatBean> poolStatBeans = Lists.newArrayListWithCapacity(16);
        int i = 1;
        while (i <= 20) {
            try {
                ObjectName objectName;
                if (i == 1) {
                    objectName = new ObjectName(JMX_NAME_BASE + jmxNamePrefix);
                } else {
                    objectName = new ObjectName(JMX_NAME_BASE + jmxNamePrefix + i);
                }
                PoolStatBean poolStatBean = buildPoolStatBean(objectName);
                poolStatBeans.add(poolStatBean);
                i++;
            } catch (InstanceNotFoundException e) {
                break;
            } catch (Exception e) {
                throw e;
            }
        }
        PoolStatBean maxPoolStatBean =
                poolStatBeans.stream().max(Comparator.comparingInt(PoolStatBean::getNumActive)).orElse(null);
        Optional.ofNullable(maxPoolStatBean).ifPresent(val -> statsPool(val));
    }

    private PoolStatBean buildPoolStatBean(ObjectName objectName) throws AttributeNotFoundException, MBeanException,
            ReflectionException, InstanceNotFoundException {
        int maxIdle = (int) mbeanServer.getAttribute(objectName, "MaxIdle");
        int minIdle = (int) mbeanServer.getAttribute(objectName, "MinIdle");
        int numActive = (int) mbeanServer.getAttribute(objectName, "NumActive");
        int numIdle = (int) mbeanServer.getAttribute(objectName, "NumIdle");
        int maxTotal = (int) mbeanServer.getAttribute(objectName, "MaxTotal");
        int nuLyaiters = (int) mbeanServer.getAttribute(objectName, "NuLyaiters");
        String jmxNamePrefix = objectName.getKeyProperty("name");
        return PoolStatBean.builder()
                .maxIdle(maxIdle)
                .minIdle(minIdle)
                .numActive(numActive)
                .numIdle(numIdle)
                .maxTotal(maxTotal)
                .nuLyaiters(nuLyaiters)
                .jmxNamePrefix(jmxNamePrefix)
                .build();
    }

    private void statsPool(PoolStatBean poolStatBean) {
        String metricPrefix = METRIC_REDIS_PREFIX + poolStatBean.getJmxNamePrefix() + '.';
        if (Objects.nonNull(statsDClient)) {
            statsDClient.recordExecutionTime(metricPrefix + "maxIdle", poolStatBean.getMaxIdle());
            statsDClient.recordExecutionTime(metricPrefix + "minIdle", poolStatBean.getMinIdle());
            statsDClient.recordExecutionTime(metricPrefix + "numActive", poolStatBean.getNumActive());
            statsDClient.recordExecutionTime(metricPrefix + "numIdle", poolStatBean.getNumIdle());
            statsDClient.recordExecutionTime(metricPrefix + "maxTotal", poolStatBean.getMaxTotal());
            statsDClient.recordExecutionTime(metricPrefix + "nuLyaiters", poolStatBean.getNuLyaiters());
            return;
        }
        StringBuilder sBuilder = new StringBuilder(16);
        sBuilder.append(metricPrefix + "maxIdle => [" + poolStatBean.getMaxIdle() + "],");
        sBuilder.append(metricPrefix + "minIdle => [" + poolStatBean.getMinIdle() + "],");
        sBuilder.append(metricPrefix + "numActive => [" + poolStatBean.getNumActive() + "],");
        sBuilder.append(metricPrefix + "numIdle => [" + poolStatBean.getNumIdle() + "],");
        sBuilder.append(metricPrefix + "maxTotal => [" + poolStatBean.getMaxTotal() + "],");
        sBuilder.append(metricPrefix + "nuLyaiters => [" + poolStatBean.getNuLyaiters() + "]");
        log.info(sBuilder.toString());
    }

    @Data
    @Builder
    private static class PoolStatBean {
        private int maxIdle;
        private int minIdle;
        private int numActive;
        private int numIdle;
        private int maxTotal;
        private int nuLyaiters;
        private String jmxNamePrefix;
    }
}
