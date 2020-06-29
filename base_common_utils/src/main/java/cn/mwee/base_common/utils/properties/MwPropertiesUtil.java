package cn.mwee.base_common.utils.properties;

import cn.mwee.base_common.utils.number.MwMoreNumberUtil;
import cn.mwee.base_common.utils.string.MwStringUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Properties;

/**
 * Created by liaomengge on 2019/9/23.
 */
@UtilityClass
public class MwPropertiesUtil {

    public String getStringProperty(Properties properties, String key) {
        return StringUtils.defaultIfBlank(getStringValue(properties, key), StringUtils.EMPTY);
    }

    public String getStringProperty(Properties properties, String key, String defaultValue) {
        return StringUtils.defaultIfBlank(getStringValue(properties, key), defaultValue);
    }

    public Double getDoubleProperty(Properties properties, String key) {
        return MwMoreNumberUtil.toDoubleObject(getStringValue(properties, key));
    }

    public Double getDoubleProperty(Properties properties, String key, Double defaultValue) {
        return MwMoreNumberUtil.toDoubleObject(getStringValue(properties, key), defaultValue);
    }

    public Long getLongProperty(Properties properties, String key) {
        return MwMoreNumberUtil.toLongObject(getStringValue(properties, key));
    }

    public Long getLongProperty(Properties properties, String key, Long defaultValue) {
        return MwMoreNumberUtil.toLongObject(getStringValue(properties, key), defaultValue);
    }

    public Integer getIntProperty(Properties properties, String key) {
        return MwMoreNumberUtil.toIntObject(getStringValue(properties, key));
    }

    public Integer getIntProperty(Properties properties, String key, Integer defaultValue) {
        return MwMoreNumberUtil.toIntObject(getStringValue(properties, key), defaultValue);
    }

    public Short getShortProperty(Properties properties, String key) {
        return MwMoreNumberUtil.toShortObject(getStringValue(properties, key));
    }

    public Short getShortProperty(Properties properties, String key, Short defaultValue) {
        return MwMoreNumberUtil.toShortObject(getStringValue(properties, key), defaultValue);
    }

    public Byte getByteProperty(Properties properties, String key) {
        return MwMoreNumberUtil.toByteObject(getStringValue(properties, key));
    }

    public Byte getByteProperty(Properties properties, String key, Byte defaultValue) {
        return MwMoreNumberUtil.toByteObject(getStringValue(properties, key), defaultValue);
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
        return MwStringUtil.getValue(properties.get(key));
    }
}
