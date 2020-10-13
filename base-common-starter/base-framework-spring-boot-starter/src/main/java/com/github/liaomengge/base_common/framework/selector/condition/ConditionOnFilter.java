package com.github.liaomengge.base_common.framework.selector.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2019/10/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnFilterCondition.class)
public @interface ConditionOnFilter {
}
