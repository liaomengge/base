package cn.ly.base_common.utils.generic;

import lombok.experimental.UtilityClass;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@UtilityClass
public class MwGenericUtil {

    public <T> Class<T> getGenericClassType(Class<?> clz) {
        Type type = clz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] types = pt.getActualTypeArguments();
            if (types.length > 0 && types[0] instanceof Class) return (Class<T>) types[0];
        }
        return (Class<T>) Object.class;
    }
}
