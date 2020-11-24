package com.github.liaomengge.base_common.support.format;

import com.google.common.base.CaseFormat;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

/**
 * Created by liaomengge on 2020/11/12.
 */
@UtilityClass
public class PropertiesFormat {

    public String getPropertyValue(Environment environment, String propertyName) {
        return getPropertyValue(environment, propertyName, "");
    }

    public String getPropertyValue(Environment environment, String propertyName, String defaultValue) {
        if (StringUtils.isBlank(propertyName)) {
            return null;
        }
        String value = environment.getProperty(propertyName);
        if (StringUtils.isBlank(value)) {
            value = environment.getProperty(convert(propertyName));
        }
        return StringUtils.defaultIfBlank(value, defaultValue);
    }

    public String convert(String propertyName) {
        if (StringUtils.isBlank(propertyName)) {
            return propertyName;
        }
        if (propertyName.contains("_")) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, propertyName);
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, propertyName);
    }
}
