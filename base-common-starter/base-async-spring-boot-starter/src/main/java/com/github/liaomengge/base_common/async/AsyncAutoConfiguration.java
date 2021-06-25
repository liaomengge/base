package com.github.liaomengge.base_common.async;

import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableAsync(proxyTargetClass = true)
public class AsyncAutoConfiguration {

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
                return (ex, method, params) -> log.error("Current Thread[{}], Invoking Async Method[{}], Exec " +
                                "Exception ===> {}", Thread.currentThread().getName(), method,
                        LyThrowableUtil.getStackTrace(ex));
            }
        };
    }
}
