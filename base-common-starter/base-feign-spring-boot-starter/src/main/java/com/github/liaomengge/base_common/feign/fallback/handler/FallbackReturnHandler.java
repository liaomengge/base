package com.github.liaomengge.base_common.feign.fallback.handler;

import feign.Target;

import java.lang.reflect.Method;

/**
 * Created by liaomengge on 2020/12/21.
 */
public interface FallbackReturnHandler<T> extends IHandler {
    T apply(Target target, Method method, Throwable throwable);
}
