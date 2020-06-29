package cn.mwee.base_common.framework.selector;

import cn.mwee.base_common.framework.selector.condition.ConditionOnFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/10/16.
 */
@Configuration
@ConditionOnFilter
@Import(FilterImportSelector.class)
public class FilterConfiguration {
}
