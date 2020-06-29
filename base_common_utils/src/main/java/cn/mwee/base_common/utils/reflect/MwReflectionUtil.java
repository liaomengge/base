package cn.mwee.base_common.utils.reflect;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by liaomengge on 17/5/12.
 */
public final class MwReflectionUtil {

    private MwReflectionUtil() {
    }

    public static final Object getFieldValue(Object obj, String fieldName) {
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

    private static String getMethodPrefix(String fieldType) {
        if ("boolean".equals(fieldType)) {
            return "is";
        }
        return "get";
    }

    private static String getMethodSuffix(String fieldName) {
        byte[] items = fieldName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }
}
