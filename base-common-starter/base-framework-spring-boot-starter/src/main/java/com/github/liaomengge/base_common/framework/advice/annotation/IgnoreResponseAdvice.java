package com.github.liaomengge.base_common.framework.advice.annotation;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2019/10/24.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IgnoreResponseAdvice {
}
