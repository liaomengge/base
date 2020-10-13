package com.github.liaomengge.base_common.framework.selector.condition;

import com.github.liaomengge.base_common.support.loader.ExtServiceLoader;
import com.github.liaomengge.service.base_framework.common.filter.chain.ServiceFilter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * Created by liaomengge on 2019/10/16.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OnFilterCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Class<ServiceFilter>> classMap =
                ExtServiceLoader.getLoader(ServiceFilter.class).getExtensionClasses();
        return MapUtils.isNotEmpty(classMap);
    }
}
