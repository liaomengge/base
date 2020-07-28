package cn.ly.base_common.influx.aspect;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by liaomengge on 2020/7/21.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableElapsedTime {
    @AliasFor("value")
    String name() default "";

    @AliasFor("name")
    String value() default "";
}
