package cn.mwee.base_common.utils.number;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Objects;

/**
 * Created by liaomengge on 17/11/25.
 */
public final class MwMoreNumberUtil {

    private MwMoreNumberUtil() {
    }

    /**
     * 判断字符串是否合法数字
     */
    public static boolean isNumber(String str) {
        return NumberUtils.isCreatable(str);
    }

    /**
     * 判断字符串是否16进制
     */
    public static boolean isHexNumber(String value) {
        int index = value.startsWith("-") ? 1 : 0;
        return value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index);
    }

    /**
     * 将10进制的String安全的转化为byte, 当str为空或非数字字符串时, 返回0
     */
    public static byte toByte(String str) {
        return NumberUtils.toByte(str, (byte) 0);
    }

    /**
     * 将10进制的String安全的转化为byte, 当str为空或非数字字符串时, 返回default值
     */
    public static byte toByte(String str, byte defaultValue) {
        return NumberUtils.toByte(str, defaultValue);
    }

    /**
     * 将10进制的String安全的转化为short, 当str为空或非数字字符串时, 返回0
     */
    public static short toShort(String str) {
        return NumberUtils.toShort(str, (short) 0);
    }

    /**
     * 将10进制的String安全的转化为short, 当str为空或非数字字符串时, 返回default值
     */
    public static short toShort(String str, short defaultValue) {
        return NumberUtils.toShort(str, defaultValue);
    }

    /**
     * 将10进制的String安全的转化为int, 当str为空或非数字字符串时, 返回0
     */
    public static int toInt(String str) {
        return NumberUtils.toInt(str, 0);
    }

    /**
     * 将10进制的String安全的转化为int, 当str为空或非数字字符串时, 返回default值
     */
    public static int toInt(String str, int defaultValue) {
        return NumberUtils.toInt(str, defaultValue);
    }

    /**
     * 将10进制的String安全的转化为long, 当str为空或非数字字符串时, 返回0
     */
    public static long toLong(String str) {
        return NumberUtils.toLong(str, 0L);
    }

    /**
     * 将10进制的String安全的转化为long, 当str为空或非数字字符串时, 返回default值
     */
    public static long toLong(String str, long defaultValue) {
        return NumberUtils.toLong(str, defaultValue);
    }

    /**
     * 将10进制的String安全的转化为double, 当str为空或非数字字符串时, 返回0
     */
    public static double toDouble(String str) {
        return NumberUtils.toDouble(str, 0L);
    }

    /**
     * 将10进制的String安全的转化为double, 当str为空或非数字字符串时, 返回default值
     */
    public static double toDouble(String str, double defaultValue) {
        return NumberUtils.toDouble(str, defaultValue);
    }

    ////////////// 10进制字符串 转换对象类型数字/////////////

    /**
     * 将10进制的String安全的转化为Byte, 当str为空或非数字字符串时, 返回null
     */
    public static Byte toByteObject(String str) {
        return toByteObject(str, null);
    }

    /**
     * 将10进制的String安全的转化为Byte, 当str为空或非数字字符串时, 返回default值
     */
    public static Byte toByteObject(String str, Byte defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Byte.valueOf(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * 将10进制的String安全的转化为Short, 当str为空或非数字字符串时, 返回null
     */
    public static Short toShortObject(String str) {
        return toShortObject(str, null);
    }

    /**
     * 将10进制的String安全的转化为Short, 当str为空或非数字字符串时, 返回default值
     */
    public static Short toShortObject(String str, Short defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Short.valueOf(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * 将10进制的String安全的转化为Integer, 当str为空或非数字字符串时, 返回null
     */
    public static Integer toIntObject(String str) {
        return toIntObject(str, null);
    }

    /**
     * 将10进制的String安全的转化为Integer, 当str为空或非数字字符串时, 返回default值
     */
    public static Integer toIntObject(String str, Integer defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * 将10进制的String安全的转化为Long, 当str为空或非数字字符串时, 返回null
     */
    public static Long toLongObject(String str) {
        return toLongObject(str, null);
    }

    /**
     * 将10进制的String安全的转化为Long, 当str为空或非数字字符串时, 返回default值
     */
    public static Long toLongObject(String str, Long defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * 将10进制的String安全的转化为Double, 当str为空或非数字字符串时, 返回null
     */
    public static Double toDoubleObject(String str) {
        return toDoubleObject(str, null);
    }

    /**
     * 将10进制的String安全的转化为Long, 当str为空或非数字字符串时, 返回default值
     */
    public static Double toDoubleObject(String str, Double defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static String toString(byte b) {
        return Byte.toString(b);
    }

    public static String toString(Byte b) {
        if (Objects.isNull(b)) {
            return "";
        }
        return b.toString();
    }

    public static String toString(short s) {
        return Short.toString(s);
    }

    public static String toString(Short s) {
        if (Objects.isNull(s)) {
            return "";
        }
        return s.toString();
    }

    public static String toString(int i) {
        return Integer.toString(i);
    }

    public static String toString(Integer i) {
        if (Objects.isNull(i)) {
            return "";
        }
        return i.toString();
    }

    public static String toString(long l) {
        return Long.toString(l);
    }

    public static String toString(Long l) {
        if (Objects.isNull(l)) {
            return "";
        }
        return l.toString();
    }

    public static String toString(double d) {
        return Double.toString(d);
    }

    public static String toString(Double d) {
        if (Objects.isNull(d)) {
            return "";
        }
        return d.toString();
    }
}
