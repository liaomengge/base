package cn.ly.base_common.framework.selector.condition;

import java.lang.annotation.*;

import org.springframework.context.annotation.Conditional;

/**
 * Created by liaomengge on 2019/10/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnFilterCondition.class)
public @interface ConditionOnFilter {
}
