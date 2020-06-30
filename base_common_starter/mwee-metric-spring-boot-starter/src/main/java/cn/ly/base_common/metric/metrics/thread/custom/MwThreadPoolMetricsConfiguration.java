package cn.ly.base_common.metric.metrics.thread.custom;

import cn.mwee.base_common.helper.concurrent.MwThreadPoolTaskWrappedExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/5/31.
 */
@Configuration
@ConditionalOnClass(MwThreadPoolTaskWrappedExecutor.class)
public class MwThreadPoolMetricsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MwThreadPoolPublicMetrics mwThreadPoolPublicMetrics() {
        return new MwThreadPoolPublicMetrics();
    }
}
