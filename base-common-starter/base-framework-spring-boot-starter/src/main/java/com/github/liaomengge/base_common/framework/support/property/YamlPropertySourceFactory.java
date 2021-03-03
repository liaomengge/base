package com.github.liaomengge.base_common.framework.support.property;

import com.github.liaomengge.base_common.utils.properties.LyResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by liaomengge on 2021/2/24.
 *
 * @PropertySource 加载指定的yml文件
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        Properties properties = LyResourceUtil.loadYml(resource);
        if (StringUtils.isBlank(name)) {
            name = getNameForResource(resource.getResource());
        }
        return new PropertiesPropertySource(name, properties);
    }

    private String getNameForResource(Resource resource) {
        String name = resource.getDescription();
        if (!org.springframework.util.StringUtils.hasText(name)) {
            name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
        }
        return name;
    }
}
