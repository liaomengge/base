package cn.ly.base_common.framework.selector;

import cn.ly.base_common.support.loader.ExtServiceLoader;
import cn.ly.service.base_framework.common.filter.chain.ServiceFilter;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * Created by liaomengge on 2019/10/16.
 */
public class FilterImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Class<ServiceFilter>> classMap =
                ExtServiceLoader.getLoader(ServiceFilter.class).getExtensionClasses();
        return classMap.keySet().stream().toArray(String[]::new);
    }
}
