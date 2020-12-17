package com.github.liaomengge.base_common.utils.reflect;

import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Created by liaomengge on 17/5/12.
 */
@UtilityClass
public class LyReflectionUtil {

    private static final Logger log = LyLogger.getInstance(LyReflectionUtil.class);

    public Object getFieldValue(Object obj, String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return null;
        }

        Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
        if (Objects.isNull(field)) {
            return null;
        }

        String fieldType = field.getType().getTypeName();
        String methodName = getMethodPrefix(fieldType) + getMethodSuffix(fieldName);
        Method method = ReflectionUtils.findMethod(obj.getClass(), methodName);
        if (Objects.isNull(method)) {
            try {
                ReflectionUtils.makeAccessible(field);
                return ReflectionUtils.getField(field, obj);
            } catch (Exception e) {
                log.warn("get field value fail", e);
                return null;
            }
        }

        Object result;
        try {
            ReflectionUtils.makeAccessible(method);
            result = method.invoke(obj);
        } catch (Exception e) {
            log.warn("get field value fail", e);
            result = null;
        }
        return result;
    }

    public void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        if (StringUtils.isBlank(fieldName) || Objects.isNull(fieldValue)) {
            return;
        }

        Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
        if (Objects.isNull(field)) {
            return;
        }

        try {
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, obj, fieldValue);
        } catch (Exception e) {
            log.warn("set field value fail", e);
        }
    }

    public Object invokeMethod(Object obj, String methodName) {
        return invokeMethod(obj, methodName, new Object[0]);
    }

    public Object invokeMethod(Object obj, String methodName, Object... methodArgs) {
        if (StringUtils.isBlank(methodName)) {
            return null;
        }
        Method method = ReflectionUtils.findMethod(obj.getClass(), methodName);
        if (Objects.isNull(method)) {
            return null;
        }
        Object result = null;
        try {
            ReflectionUtils.makeAccessible(method);
            result = ReflectionUtils.invokeMethod(method, obj, methodArgs);
        } catch (Exception e) {
            log.warn("get field value fail", e);
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
