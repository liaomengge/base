package cn.ly.base_common.async;

import cn.mwee.base_common.utils.error.MwThrowableUtil;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import org.slf4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

/**
 * Created by liaomengge on 2019/2/21.
 */
@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncAutoConfiguration {

    private static final Logger logger = MwLogger.getInstance(AsyncAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public AsyncConfigurer asyncConfigurer() {
        return new AsyncConfigurer() {
            @Override
            public Executor getAsyncExecutor() {
                return null;
            }

            @Override
            public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
                return (ex, method, params) -> logger.error("Current Thread[{}], Invoking Async Method[{}], Exec " +
                                "Exception ===> {}", Thread.currentThread().getName(), method,
                        MwThrowableUtil.getStackTrace(ex));
            }
        };
    }
}
