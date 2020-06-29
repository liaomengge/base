package cn.mwee.base_common.framework.selector;

import cn.mwee.base_common.support.loader.ExtServiceLoader;
import cn.mwee.service.base_framework.common.filter.chain.ServiceFilter;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * Created by liaomengge on 2019/10/16.
 */
public class FilterImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Class<?>> classMap = ExtServiceLoader.getInstance(ServiceFilter.class).getExtensionClasses();
        return classMap.keySet().stream().toArray(String[]::new);
    }
}
