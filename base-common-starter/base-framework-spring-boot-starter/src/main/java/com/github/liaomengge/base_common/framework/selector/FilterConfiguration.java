package com.github.liaomengge.base_common.framework.selector;

import com.github.liaomengge.base_common.framework.selector.condition.ConditionOnFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/10/16.
 */
@Configuration(proxyBeanMethods = false)
@ConditionOnFilter
@Import(FilterImportSelector.class)
public class FilterConfiguration {
}
