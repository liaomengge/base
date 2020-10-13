package com.github.liaomengge.base_common.utils.reflect;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

/**
 * Created by liaomengge on 17/5/12.
 */
@UtilityClass
public class LyReflectionUtil {

    public final Object getFieldValue(Object obj, String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return null;
        }

        Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
        if (field == null) {
            return null;
        }

        String fieldType = field.getType().getTypeName();
        String methodName = getMethodPrefix(fieldType) + getMethodSuffix(fieldName);
        Method method = ReflectionUtils.findMethod(obj.getClass(), methodName);
        if (method == null) {
            return null;
        }

        Object result;
        try {
            result = method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            result = null;
        }
        return result;
    }

    private String getMethodPrefix(String fieldType) {
        if ("boolean".equals(fieldType)) {
            return "is";
        }
        return "get";
    }

    private String getMethodSuffix(String fieldName) {
        byte[] items = fieldName.getBytes(Charset.defaultCharset());
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items, Charset.defaultCharset());
    }
}
