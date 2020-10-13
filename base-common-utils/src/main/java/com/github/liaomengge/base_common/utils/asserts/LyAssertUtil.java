package com.github.liaomengge.base_common.utils.asserts;

import com.github.liaomengge.base_common.support.exception.ParamException;

import com.google.common.base.Preconditions;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 16/4/12.
 */
@UtilityClass
public class LyAssertUtil {

    /**
     * 断言表达式的值为true, 否则抛出包含指定错误的信息
     *
     * @param expValue 断言表达式值
     * @param errCode  错误码
     * @param errMsg   错误信息
     * @throws ParamException
     */
    public void isTrue(boolean expValue, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(expValue), errCode, errMsg, params);
    }

    /**
     * 断言表达式的值为false, 否则抛出包含指定错误的信息
     *
     * @param expValue 断言表达式值
     * @param errCode  错误码
     * @param errMsg   错误信息
     * @throws ParamException
     */
    public void isFalse(boolean expValue, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(!expValue), errCode, errMsg, params);
    }

    /**
     * 断言两个对象相等, 否则抛出包含指定错误的信息
     *
     * @param obj1    待比较对象1
     * @param obj2    待比较对象2
     * @param errCode 错误码
     * @param errMsg  错误信息
     * @throws ParamException
     */
    public void equals(Object obj1, Object obj2, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> {
            if (obj1 == null) {
                Preconditions.checkArgument(obj2 == null);
                return;
            }
            Preconditions.checkArgument(obj1.equals(obj2));
        }, errCode, errMsg, params);

    }

    /**
     * 断言两个对象不等, 否则抛出包含指定错误的信息
     *
     * @param obj1    待比较对象1
     * @param obj2    带比较对象2
     * @param errCode 错误码
     * @param errMsg  错误信息
     * @throws ParamException
     */
    public void notEquals(Object obj1, Object obj2, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> {
            if (obj1 == null) {
                Preconditions.checkArgument(obj2 != null);
                return;
            }
            Preconditions.checkArgument(!obj1.equals(obj2));
        }, errCode, errMsg, params);
    }

    /**
     * 断言对象为空, 否则抛出包含指定错误的信息
     *
     * @param str     断言字符串
     * @param errCode 错误码
     * @param errMsg  错误信息
     * @param params
     * @throws ParamException
     */
    public void isBlank(String str, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(StringUtils.isBlank(str)), errCode, errMsg, params);
    }

    /**
     * 断言对象非空, 否则抛出包含指定错误的信息
     *
     * @param str     断言字符串
     * @param errCode 错误码
     * @param errMsg  错误信息
     * @throws ParamException
     */
    public void isNotBlank(String str, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(StringUtils.isNotBlank(str)), errCode, errMsg, params);
    }

    /**
     * 断言对象为null, 否则抛出包含指定错误的信息
     *
     * @param object  断言的对象
     * @param errCode 错误码
     * @param errMsg  错误信息
     * @throws ParamException
     */
    public void isNull(Object object, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(object == null), errCode, errMsg, params);
    }

    /**
     * 断言对象非null, 否则抛出包含指定错误的信息
     *
     * @param object  断言对象
     * @param errCode 错误码
     * @param errMsg  错误信息
     * @throws ParamException
     */
    public void notNull(Object object, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(object != null), errCode, errMsg, params);
    }

    /**
     * 断言集合不为空或null, 否则抛出包含指定错误的信息
     *
     * @param collection 断言集合
     * @param errCode    错误码
     * @param errMsg     错误信息
     * @throws ParamException
     */
    public void notEmpty(Collection collection, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(!CollectionUtils.isEmpty(collection)), errCode, errMsg, params);
    }

    /**
     * 断言集合为空或null, 否则抛出包含指定错误的信息
     *
     * @param collection 断言集合
     * @param errCode    错误码
     * @param errMsg     错误信息
     * @throws ParamException
     */
    public void isEmpty(Collection collection, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(CollectionUtils.isEmpty(collection)), errCode, errMsg, params);
    }

    /**
     * 断言map不为空或null, 否则抛出包含指定错误的信息
     *
     * @param map     断言map
     * @param errCode 错误码
     * @param errMsg  错误信息
     * @throws ParamException
     */
    public void notEmpty(Map map, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(MapUtils.isNotEmpty(map)), errCode, errMsg, params);
    }

    /**
     * 断言map为空或null, 否则抛出包含指定错误的信息
     *
     * @param map     断言map
     * @param errCode 错误码
     * @param errMsg  错误信息
     * @throws ParamException
     */
    public void isEmpty(Map map, int errCode, String errMsg, String... params) throws ParamException {
        check(() -> Preconditions.checkArgument(MapUtils.isEmpty(map)), errCode, errMsg, params);
    }

    /**
     * 断言实现
     * `
     *
     * @param assertTemplate
     * @param errCode
     * @param errMsg
     * @param params
     * @throws ParamException
     */
    private void check(AssertTemplate assertTemplate, int errCode, String errMsg, String... params) throws ParamException {
        try {
            assertTemplate.doAssert();
        } catch (IllegalArgumentException e) {
            if (ArrayUtils.isEmpty(params)) {
                throw new ParamException(String.valueOf(errCode), errMsg);
            }
            Object[] objs = params;
            throw new ParamException(String.valueOf(errCode), MessageFormat.format(errMsg, objs));
        }
    }

    @FunctionalInterface
    private interface AssertTemplate {

        /**
         * 实际断言操作。
         */
        void doAssert();

    }
}
