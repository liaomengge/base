package cn.ly.base_common.metric.threadpool;

import cn.ly.base_common.helper.concurrent.LyThreadPoolTaskWrappedExecutor;
import cn.ly.base_common.helper.concurrent.LyThreadPoolWrappedExecutor;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.experimental.UtilityClass;

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
