package cn.ly.base_common.dayu.custom.annotation;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2019/10/30.
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreakerResource {

    String value() default "";

    String fallback() default "";

    Class<?> fallbackClass();
}
