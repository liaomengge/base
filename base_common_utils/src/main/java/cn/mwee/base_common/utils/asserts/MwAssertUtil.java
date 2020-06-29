package cn.mwee.base_common.utils.asserts;

import cn.mwee.base_common.support.exception.ParamException;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Created by liaomengge on 16/4/12.
 */
public final class MwAssertUtil {

    private MwAssertUtil() {
    }

    /**
     * 断言表达式的值为true, 否则抛出包含指定错误的信息
     *
     * @param expValue 断言表达式值
     * @param errCode  错误码
     * @param errMsg   错误信息
     * @throws ParamException
     */
    public static void isTrue(final boolean expValue, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void isFalse(final boolean expValue, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void equals(final Object obj1, final Object obj2, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void notEquals(final Object obj1, final Object obj2, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void isBlank(final String str, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void isNotBlank(final String str, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void isNull(final Object object, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void notNull(final Object object, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void notEmpty(final Collection collection, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void isEmpty(final Collection collection, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void notEmpty(final Map map, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    public static void isEmpty(final Map map, final int errCode, final String errMsg, final String... params) throws ParamException {
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
    private static void check(AssertTemplate assertTemplate, final int errCode, final String errMsg, final String... params) throws ParamException {
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
