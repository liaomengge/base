package cn.ly.base_common.endpoint.condition;

import java.lang.annotation.*;

import org.springframework.context.annotation.Conditional;

/**
 * Created by liaomengge on 2019/7/4.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnCustomPropertyConditional.class)
public @interface ConditionalOnCustomProperty {
}
