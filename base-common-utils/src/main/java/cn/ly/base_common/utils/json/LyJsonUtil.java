package cn.ly.base_common.utils.json;

import cn.ly.base_common.utils.date.LyDateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Created by liaomengge on 17/11/8.
 */
@UtilityClass
public class LyJsonUtil {

    /**
     * 是否是json字符串
     *
     * @param str
     * @return
     */
    public boolean isJson(String str) {
        try {
            fromJson(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 序列化对象
     * <p>
     * null对象直接返回null
     *
     * @param object
     * @return
     */
    public String toJson(Object object) {
        if (null == object) {
            return null;
        }
        return JSON.toJSONString(object);
    }

    /**
     * 序列化对象,指定SerializerFeature属性 - 关闭循环引用
     * <p>
     * null对象直接返回null
     *
     * @param object
     * @return
     */
    public String toJson4Log(Object object) {
        if (null == object) {
            return null;
        }
        if (object instanceof String) {
            return (String) object;
        }
        return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 序列化对象,指定SerializerFeature属性
     * <p>
     * null对象直接返回null
     *
     * @param object
     * @param features
     * @return
     */
    public String toJson(Object object, SerializerFeature... features) {
        if (null == object) {
            return null;
        }
        return JSON.toJSONString(object, features);
    }

    /**
     * 默认以'yyyy-MM-dd HH:mm:ss'格式序列化
     *
     * @param object
     * @return
     */
    public String toJson4DateFormat(Object object) {
        return toJson4DateFormat(object, Collections.EMPTY_MAP);
    }

    /**
     * 只针对日期处理
     * valueFormat表示对应的字段和格式,for example: {"birthday", "yyyy-MM-dd"}
     * 1. valueFormat为null, 则日期将自动转成序列化成long类型
     * 2. valueFormat为空map或者不包含的日期字段, 则日期均将默认以'yyyy-MM-dd HH:mm:ss'的格式学序列化
     * 3. valueFormat为非空map, 则日期将以指定的格式进行序列化
     *
     * @param object
     * @param valueFormat
     * @return
     */
    public String toJson4DateFormat(Object object, Map<String, Object> valueFormat) {
        if (valueFormat == null || object == null) {
            return toJson(object);
        }
        ValueFilter valueFilter = (obj, propertyName, propertyValue) -> {
            if (valueFormat.containsKey(propertyName)) {
                return LyDateUtil.getDate2String((Date) propertyValue, valueFormat.get(propertyName).toString());
            }
            return propertyValue;
        };

        return JSON.toJSONString(object, valueFilter);
    }

    /**
     * 序列化字段属性忽略,推荐使用@JsonIgnore, 但是为了保证不侵入entry, 也可以使用代码形式
     *
     * @param object
     * @param ignorePropertyNames
     * @return
     */
    public String toJsonIgnoreProperty(Object object, String... ignorePropertyNames) {
        if (ArrayUtils.isEmpty(ignorePropertyNames) || object == null) {
            return toJson(object);
        }
        PropertyFilter propertyFilter = (obj, propertyName, propertyValue) -> {
            if (ArrayUtils.indexOf(ignorePropertyNames, propertyName) == -1) {
                return true;
            }
            return false;
        };
        return JSON.toJSONString(object, propertyFilter);
    }

    /**
     * 序列化包含的字段, 避免大量无用的字段写入缓存
     *
     * @param object
     * @param includePropertyNames
     * @return
     */
    public String toJsonIncludeProperty(Object object, String... includePropertyNames) {
        if (ArrayUtils.isEmpty(includePropertyNames) || object == null) {
            return toJson(object);
        }
        PropertyFilter propertyFilter = (obj, propertyName, propertyValue) -> {
            if (ArrayUtils.indexOf(includePropertyNames, propertyName) != -1) {
                return true;
            }
            return false;
        };
        return JSON.toJSONString(object, propertyFilter);
    }

    /**
     * 类似@JSONField, 用于避免实体类污染
     *
     * @param object
     * @param replacePropertyMap
     * @return
     */
    public String toJsonReplaceProperty(Object object, Map<String, Object> replacePropertyMap) {
        if (MapUtils.isEmpty(replacePropertyMap) || object == null) {
            return toJson(object);
        }

        NameFilter nameFilter = (object1, propertyName, propertyValue) -> {
            if (replacePropertyMap.containsKey(propertyName)) {
                return replacePropertyMap.get(propertyName).toString();
            }
            return propertyName;
        };

        return JSON.toJSONString(object, nameFilter);
    }

    public Object fromJson(String jsonString) {
        return JSON.parse(jsonString);
    }

    public <T> T fromJson(String jsonString, Class<T> clz) {
        return JSON.parseObject(jsonString, clz);
    }

    public <T> T fromJson(String jsonString, TypeReference<T> typeReference) {
        return JSON.parseObject(jsonString, typeReference);
    }
}
