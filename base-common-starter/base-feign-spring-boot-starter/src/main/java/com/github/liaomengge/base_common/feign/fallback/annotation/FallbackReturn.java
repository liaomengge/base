package com.github.liaomengge.base_common.feign.fallback.annotation;

import com.github.liaomengge.base_common.feign.fallback.handler.FallbackReturnHandler;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2020/12/21.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FallbackReturn {
    Class<? extends FallbackReturnHandler> handler();
}
