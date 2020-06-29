package cn.mwee.base_common.utils.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class MwGenericUtil {

    private MwGenericUtil() {
    }

    public static <T> Class<T> getGenericClassType(Class<?> clz) {
        Type type = clz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] types = pt.getActualTypeArguments();
            if (types.length > 0 && types[0] instanceof Class) {
                return (Class<T>) types[0];
            }
        }
        return (Class<T>) Object.class;
    }
}
