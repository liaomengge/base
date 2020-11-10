package com.github.liaomengge.base_common.utils.generic;

import lombok.experimental.UtilityClass;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@UtilityClass
public class LyGenericUtil {

    /**
     * 获取父接口泛型类型
     *
     * @param clz
     * @param <T>
     * @return
     */
    public <T> Class<T> getActualTypeArguments4GenericInterface(Class<?> clz) {
        Type[] genericInterfaces = clz.getGenericInterfaces();
        if (genericInterfaces[0] instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                if (actualTypeArguments[0] instanceof Class) {
                    return (Class<T>) actualTypeArguments[0];
                }
                if (actualTypeArguments[0] instanceof ParameterizedType) {
                    ParameterizedType parameterizedType2 = (ParameterizedType) actualTypeArguments[0];
                    return (Class<T>) parameterizedType2.getRawType();
                }
            }
        }
        return (Class<T>) Object.class;
    }

    /**
     * 获取父类泛型类型
     *
     * @param clz
     * @param <T>
     * @return
     */
    public <T> Class<T> getActualTypeArguments4GenericClass(Class<?> clz) {
        Type genericSuperclass = clz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                if (actualTypeArguments[0] instanceof Class) {
                    return (Class<T>) actualTypeArguments[0];
                }
                if (actualTypeArguments[0] instanceof ParameterizedType) {
                    ParameterizedType parameterizedType2 = (ParameterizedType) actualTypeArguments[0];
                    return (Class<T>) parameterizedType2.getRawType();
                }
            }
        }
        return (Class<T>) Object.class;
    }
}
