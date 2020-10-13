package com.github.liaomengge.base_common.utils.properties;

import com.github.liaomengge.base_common.utils.number.LyMoreNumberUtil;
import com.github.liaomengge.base_common.utils.string.LyStringUtil;

import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/9/23.
 */
@UtilityClass
public class LyPropertiesUtil {

    public String getStringProperty(Properties properties, String key) {
        return StringUtils.defaultIfBlank(getStringValue(properties, key), StringUtils.EMPTY);
    }

    public String getStringProperty(Properties properties, String key, String defaultValue) {
        return StringUtils.defaultIfBlank(getStringValue(properties, key), defaultValue);
    }

    public Double getDoubleProperty(Properties properties, String key) {
        return LyMoreNumberUtil.toDoubleObject(getStringValue(properties, key));
    }

    public Double getDoubleProperty(Properties properties, String key, Double defaultValue) {
        return LyMoreNumberUtil.toDoubleObject(getStringValue(properties, key), defaultValue);
    }

    public Long getLongProperty(Properties properties, String key) {
        return LyMoreNumberUtil.toLongObject(getStringValue(properties, key));
    }

    public Long getLongProperty(Properties properties, String key, Long defaultValue) {
        return LyMoreNumberUtil.toLongObject(getStringValue(properties, key), defaultValue);
    }

    public Integer getIntProperty(Properties properties, String key) {
        return LyMoreNumberUtil.toIntObject(getStringValue(properties, key));
    }

    public Integer getIntProperty(Properties properties, String key, Integer defaultValue) {
        return LyMoreNumberUtil.toIntObject(getStringValue(properties, key), defaultValue);
    }

    public Short getShortProperty(Properties properties, String key) {
        return LyMoreNumberUtil.toShortObject(getStringValue(properties, key));
    }

    public Short getShortProperty(Properties properties, String key, Short defaultValue) {
        return LyMoreNumberUtil.toShortObject(getStringValue(properties, key), defaultValue);
    }

    public Byte getByteProperty(Properties properties, String key) {
        return LyMoreNumberUtil.toByteObject(getStringValue(properties, key));
    }

    public Byte getByteProperty(Properties properties, String key, Byte defaultValue) {
        return LyMoreNumberUtil.toByteObject(getStringValue(properties, key), defaultValue);
    }

    public Boolean getBooleanProperty(Properties properties, String key) {
        return BooleanUtils.toBooleanObject(getStringValue(properties, key));
    }

    public Boolean getBooleanProperty(Properties properties, String key, Boolean defaultValue) {
        return BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBooleanObject(getStringValue(properties, key)),
                defaultValue);
    }

    private String getStringValue(Properties properties, String key) {
        Object value = properties.get(key);
        if (Objects.isNull(value)) {
            return null;
        }
        return LyStringUtil.getValue(properties.get(key));
    }
}
