package com.github.liaomengge.base_common.support.resolve;

import com.google.common.base.CaseFormat;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

/**
 * Created by liaomengge on 2020/11/12.
 */
@UtilityClass
public class PropertiesResolve {

    public String getPropertyValue(Environment environment, String propertyName) {
        return getPropertyValue(environment, propertyName, "");
    }

    public String getPropertyValue(Environment environment, String propertyName, String defaultValue) {
        if (StringUtils.isBlank(propertyName)) {
            return null;
        }
        String value = environment.getProperty(propertyName);
        if (StringUtils.isBlank(value)) {
            value = environment.getProperty(resolve(propertyName));
        }
        return StringUtils.defaultIfBlank(value, defaultValue);
    }

    private String resolve(String propertyName) {
        if (StringUtils.isBlank(propertyName)) {
            return propertyName;
        }
        if (propertyName.contains("_")) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, propertyName);
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, propertyName);
    }
}
