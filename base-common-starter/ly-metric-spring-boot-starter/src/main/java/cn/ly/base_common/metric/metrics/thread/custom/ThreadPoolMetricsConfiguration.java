//package cn.ly.base_common.metric.metrics.thread.custom;
//
//import cn.ly.base_common.helper.concurrent.LyThreadPoolTaskWrappedExecutor;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Created by liaomengge on 2019/5/31.
// */
//@Configuration
//@ConditionalOnClass(LyThreadPoolTaskWrappedExecutor.class)
//public class ThreadPoolMetricsConfiguration {
//
//    @Bean
//    @ConditionalOnMissingBean
//    public ThreadPoolPublicMetrics lyThreadPoolPublicMetrics() {
//        return new ThreadPoolPublicMetrics();
//    }
//}
