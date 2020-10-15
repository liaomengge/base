package com.github.liaomengge.service.base_framework.common.annotation;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2019/10/24.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreServiceApiAop {

    boolean ignoreArgs() default false;

    boolean ignoreResult() default false;
}
