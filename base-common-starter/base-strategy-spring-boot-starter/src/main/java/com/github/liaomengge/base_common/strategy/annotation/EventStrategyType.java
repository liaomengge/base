package com.github.liaomengge.base_common.strategy.annotation;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2021/6/3.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventStrategyType {

    String value() default "";

    String category() default "";
}
