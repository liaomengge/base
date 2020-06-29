package cn.mwee.base_common.utils.collection;

import cn.mwee.base_common.utils.json.MwJacksonUtil;
import cn.mwee.base_common.utils.json.MwJsonUtil;
import cn.mwee.base_common.utils.string.MwStringUtil;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 16/5/10.
 */
public final class MwMapUtil {

    private static final String PROPERTY_NAME = "class";

    private MwMapUtil() {
    }

    /**********************************************Map的集合操作********************************************/

    /**
     * 对两个Map进行比较, 返回MapDifference, 然后各种妙用.
     * <p>
     * 包括key的差集, key的交集, 以及key相同但value不同的元素。
     */
    public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left,
                                                        Map<? extends K, ? extends V> right) {
        return Maps.difference(left, right);
    }

    /**
     * Bean 2 Map, 只针对非嵌套的简单的javaBean, 对复杂的javaBean建议使用序列化和发序列化
     *
     * @param bean
     * @param excludePropertyName
     * @return
     */
    public static Map<String, Object> bean2Map(Object bean, String... excludePropertyName) {
        if (null == bean) {
            return Maps.newHashMap();
        }

        Class<?> type = bean.getClass();
        Map<String, Object> resultMap = Maps.newHashMap();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            return null;
        }

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        PropertyDescriptor descriptor;
        String propertyName;
        for (int i = 0, length = propertyDescriptors.length; i < length; i++) {
            descriptor = propertyDescriptors[i];
            propertyName = descriptor.getName();

            if (!PROPERTY_NAME.equals(propertyName) && !ArrayUtils.contains(excludePropertyName, propertyName)) {
                Method readMethod = descriptor.getReadMethod();
                Object result;
                try {
                    result = readMethod.invoke(bean, new Object[0]);
                } catch (ReflectiveOperationException e) {
                    return null;
                }
                resultMap.put(propertyName, result);
            }
        }
        return resultMap;
    }

    /**
     * Bean 2 Map, 使用Cglib 高效处理
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> bean2Map4Cglib(Object obj) {
        if (null == obj) {
            return Maps.newHashMap();
        }

        Map<String, Object> resultMap = Maps.newHashMap();

        BeanMap beanMap = BeanMap.create(obj);
        for (Object key : beanMap.keySet()) {
            resultMap.put(MwStringUtil.getValue(key), beanMap.get(key));
        }
        return resultMap;
    }

    /**
     * Bean 2 Map, 针对复杂的JavaBean处理,使用FastJson序列化和反序列化
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> bean2Map4Json(Object obj) {
        if (null == obj) {
            return Maps.newHashMap();
        }
        return MwJsonUtil.fromJson(MwJsonUtil.toJson(obj), new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * Bean 2 Map, 针对简单的JavaBean处理,可以使用FastJson注解的字段
     *
     * @param obj
     * @param isFastJsonField
     * @param excludePropertyName
     * @return
     */
    public static Map<String, Object> bean2Map4FastJson(Object obj, boolean isFastJsonField, String... excludePropertyName) {
        if (null == obj) {
            return Maps.newHashMap();
        }

        Class<?> type = obj.getClass();
        Map<String, Object> resultMap = Maps.newHashMap();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            return null;
        }

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        PropertyDescriptor descriptor;
        String propertyName;
        for (int i = 0, length = propertyDescriptors.length; i < length; i++) {
            descriptor = propertyDescriptors[i];
            propertyName = descriptor.getName();

            if (!PROPERTY_NAME.equals(propertyName) && !ArrayUtils.contains(excludePropertyName, propertyName)) {
                Method readMethod = descriptor.getReadMethod();
                Object result;
                Field field;
                try {
                    result = readMethod.invoke(obj, new Object[0]);
                    field = type.getDeclaredField(propertyName);
                } catch (ReflectiveOperationException e) {
                    return null;
                }

                if (isFastJsonField) {
                    if (field != null) {
                        JSONField jsonField = field.getAnnotation(JSONField.class);
                        if (jsonField != null) {
                            String annotationPropertyName = jsonField.name();
                            if (StringUtils.isNotBlank(annotationPropertyName)) {
                                propertyName = annotationPropertyName;
                            }
                        }
                    }
                }
                resultMap.put(propertyName, result);
            }
        }
        return resultMap;
    }

    /**
     * Bean 2 Map, 针对简单的JavaBean处理,可以使用Jackson注解的字段
     *
     * @param obj
     * @param isJacksonField
     * @param excludePropertyName
     * @return
     */
    public static Map<String, Object> bean2Map4Jackson(Object obj, boolean isJacksonField, String... excludePropertyName) {
        if (null == obj) {
            return Maps.newHashMap();
        }

        Class<?> type = obj.getClass();
        Map<String, Object> resultMap = Maps.newHashMap();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            return null;
        }

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        PropertyDescriptor descriptor;
        String propertyName;
        for (int i = 0, length = propertyDescriptors.length; i < length; i++) {
            descriptor = propertyDescriptors[i];
            propertyName = descriptor.getName();

            if (!PROPERTY_NAME.equals(propertyName) && !ArrayUtils.contains(excludePropertyName, propertyName)) {
                Method readMethod = descriptor.getReadMethod();
                Object result;
                Field field;
                try {
                    result = readMethod.invoke(obj, new Object[0]);
                    field = type.getDeclaredField(propertyName);
                } catch (ReflectiveOperationException e) {
                    return null;
                }

                if (isJacksonField) {
                    if (field != null) {
                        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                        if (jsonProperty != null) {
                            String annotationPropertyName = jsonProperty.value();
                            if (StringUtils.isNotBlank(annotationPropertyName)) {
                                propertyName = annotationPropertyName;
                            }
                        }
                    }
                }
                resultMap.put(propertyName, result);
            }
        }
        return resultMap;
    }

    /**
     * Bean 2 Map, 针对简单的JavaBean处理
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> bean2Map4Jackson(Object obj) {
        if (null == obj) {
            return null;
        }

        return MwJacksonUtil.bean2Map(obj, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * Map 2 Bean 依据指定类型转换
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz) {
        if (null == map) {
            return null;
        }

        return MwJacksonUtil.map2Bean(map, clazz);
    }

    /**
     * Map 2 Bean 依据指定类型转换2
     *
     * @param map
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T map2Bean(Map<String, Object> map, com.fasterxml.jackson.core.type.TypeReference<?> typeReference) {
        if (null == map) {
            return null;
        }

        return MwJacksonUtil.map2Bean(map, typeReference);
    }

    /**
     * Map 2 Bean, 如果转换失败,则对象还是原来对象,不会throw exception
     *
     * @param map
     * @param object
     */
    public static void map2Bean(Map<String, Object> map, Object object) {
        if (null == map || null == object) {
            return;
        }

        try {
            BeanUtils.populate(object, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
        }
    }

    /**
     * Map 2 Bean, 如果转换失败,则对象还是原来对象, 会throw exception
     *
     * @param map
     * @param object
     */
    public static void map2BeanThrowEx(Map<String, Object> map, Object object) throws InvocationTargetException, IllegalAccessException {
        if (null == map || null == object) {
            return;
        }

        BeanUtils.populate(object, map);
    }

    /**********************************************高级特性********************************************/

    /**
     * 行转列, 合并相同的键, 值合并为列表<br>
     * 将Map列表中相同key的值组成列表做为Map的value<br>
     * 是{@link #toMapList(Map)}的逆方法<br>
     * 比如传入数据：
     * <p>
     * <pre>
     * [
     *  {a: 1, b: 1, c: 1}
     *  {a: 2, b: 2}
     *  {a: 3, b: 3}
     *  {a: 4}
     * ]
     * </pre>
     * <p>
     * 结果是：
     * <p>
     * <pre>
     * {
     *   a: [1,2,3,4]
     *   b: [1,2,3,]
     *   c: [1]
     * }
     * </pre>
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param mapList Map列表
     * @return Map
     */
    public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
        final HashMap<K, List<V>> resultMap = new HashMap<>();
        if (IterableUtils.isEmpty(mapList)) {
            return resultMap;
        }

        Set<Map.Entry<K, V>> entrySet;
        for (Map<K, V> map : mapList) {
            entrySet = map.entrySet();
            K key;
            List<V> valueList;
            for (Map.Entry<K, V> entry : entrySet) {
                key = entry.getKey();
                valueList = resultMap.get(key);
                if (null == valueList) {
                    valueList = Lists.newArrayList(entry.getValue());
                    resultMap.put(key, valueList);
                } else {
                    valueList.add(entry.getValue());
                }
            }
        }

        return resultMap;
    }

    /**
     * 列转行。将Map中值列表分别按照其位置与key组成新的map。<br>
     * 是{@link #toListMap(Iterable)}的逆方法<br>
     * 比如传入数据：
     * <p>
     * <pre>
     * {
     *   a: [1,2,3,4]
     *   b: [1,2,3,]
     *   c: [1]
     * }
     * </pre>
     * <p>
     * 结果是：
     * <p>
     * <pre>
     * [
     *  {a: 1, b: 1, c: 1}
     *  {a: 2, b: 2}
     *  {a: 3, b: 3}
     *  {a: 4}
     * ]
     * </pre>
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param listMap 列表Map
     * @return Map列表
     */
    public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
        final List<Map<K, V>> resultList = new ArrayList<>();
        if (MapUtils.isEmpty(listMap)) {
            return resultList;
        }

        boolean isEnd;// 是否结束。标准是元素列表已耗尽
        int index = 0;// 值索引
        Map<K, V> map;
        do {
            isEnd = true;
            map = new HashMap<>();
            List<V> vList;
            int vListSize;
            for (Map.Entry<K, ? extends Iterable<V>> entry : listMap.entrySet()) {
                vList = Lists.newArrayList(entry.getValue());
                vListSize = vList.size();
                if (index < vListSize) {
                    map.put(entry.getKey(), vList.get(index));
                    if (index != vListSize - 1) {
                        // 当值列表中还有更多值（非最后一个）, 继续循环
                        isEnd = false;
                    }
                }
            }
            if (false == map.isEmpty()) {
                resultList.add(map);
            }
            index++;
        } while (false == isEnd);

        return resultList;
    }

    // ----------------------------------------------------------------------------------------------- join

    /**
     * 将map转成字符串
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @return 连接字符串
     * @since 3.1.1
     */
    public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator) {
        return join(map, separator, keyValueSeparator, false);
    }

    /**
     * 将map转成字符串, 忽略null的键和值
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @return 连接后的字符串
     * @since 3.1.1
     */
    public static <K, V> String joinIgnoreNull(Map<K, V> map, String separator, String keyValueSeparator) {
        return join(map, separator, keyValueSeparator, true);
    }

    /**
     * 将map转成字符串
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @param isIgnoreNull      是否忽略null的键和值
     * @return 连接后的字符串
     * @since 3.1.1
     */
    public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, boolean isIgnoreNull) {
        final StringBuilder strBuilder = new StringBuilder(16);
        boolean isFirst = true;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (false == isIgnoreNull || entry.getKey() != null && entry.getValue() != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    strBuilder.append(separator);
                }
                strBuilder.append(Objects.toString(entry.getKey(), "")).append(keyValueSeparator).append(Objects.toString(entry.getValue(), ""));
            }
        }
        return strBuilder.toString();
    }

    public static <K, V> Map<K, V> filterByKey(Map<K, V> map, Predicate<V> predicate) {
        return map.entrySet()
                .stream()
                .filter(x -> predicate.test(x.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<K> predicate) {
        return map.entrySet()
                .stream()
                .filter(x -> predicate.test(x.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
