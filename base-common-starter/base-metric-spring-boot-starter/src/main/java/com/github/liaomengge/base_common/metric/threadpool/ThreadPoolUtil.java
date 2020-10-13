package com.github.liaomengge.base_common.metric.threadpool;

import com.github.liaomengge.base_common.helper.concurrent.LyThreadPoolTaskWrappedExecutor;
import com.github.liaomengge.base_common.helper.concurrent.LyThreadPoolWrappedExecutor;
import lombok.experimental.UtilityClass;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by liaomengge on 2020/9/18.
 */
@UtilityClass
public class ThreadPoolUtil {

    protected ThreadPoolExecutor unwrap(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return threadPoolTaskExecutor.getThreadPoolExecutor();
    }

    protected ThreadPoolExecutor unwrap(LyThreadPoolTaskWrappedExecutor lyThreadPoolTaskWrappedExecutor) {
        ThreadPoolTaskExecutor taskExecutor = lyThreadPoolTaskWrappedExecutor.getThreadPoolTaskExecutor();
        return taskExecutor.getThreadPoolExecutor();
    }

    protected ThreadPoolExecutor unwrap(LyThreadPoolWrappedExecutor lyThreadPoolWrappedExecutor) {
        return lyThreadPoolWrappedExecutor.getThreadPoolExecutor();
    }
}
