package com.github.liaomengge.base_common.utils.shutdown;

import java.util.function.Consumer;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2018/12/20.
 */
@UtilityClass
public class LyShutdownUtil {

    public <T> void registerShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }

    public <T> void registerShutdownHook(T t, Consumer<T> consumer) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> consumer.accept(t)));
    }
}
