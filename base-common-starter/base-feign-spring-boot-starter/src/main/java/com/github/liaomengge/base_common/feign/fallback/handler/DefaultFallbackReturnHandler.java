package com.github.liaomengge.base_common.feign.fallback.handler;

import feign.Target;

import java.lang.reflect.Method;

/**
 * Created by liaomengge on 2020/12/29.
 */
public class DefaultFallbackReturnHandler implements FallbackReturnHandler {

    @Override
    public Object apply(Target target, Method method, Throwable throwable) {
        return null;
    }
}
