package cn.ly.base_common.endpoint.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2019/7/4.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnCustomPropertyConditional.class)
public @interface ConditionalOnCustomProperty {
}
