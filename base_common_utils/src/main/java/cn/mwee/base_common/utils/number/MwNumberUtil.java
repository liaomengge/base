package cn.mwee.base_common.utils.number;


import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MwNumberUtil {

    private MwNumberUtil() {
        //工具类无需对象实例化
    }

    public static long getLongValue(Long v) {
        if (v == null) {
            return 0;
        }
        return v.longValue();
    }

    public static long getLongValue(Long v, Long defaultVal) {
        if (v == null) {
            return getLongValue(defaultVal);
        }
        return v.longValue();
    }

    public static long getLongValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.longValue();
    }

    public static long getLongValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.longValue();
    }

    public static long getLongValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.longValue();
    }

    public static long getLongValue(BigDecimal v) {
        if (v == null) {
            return 0;
        }
        return v.longValue();
    }

    public static int getIntValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public static int getIntValue(Integer b, Integer defaultVal) {
        if (b == null) {
            return getIntValue(defaultVal);
        }
        return b.intValue();
    }

    public static int getIntValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public static int getIntValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public static int getIntValue(Long b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public static int getIntValue(BigDecimal b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public static short getShortValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public static double getShortValue(Short b, Short defaultVal) {
        if (b == null) {
            return getShortValue(defaultVal);
        }
        return b.doubleValue();
    }

    public static short getShortValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public static short getShortValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public static short getShortValue(Long b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public static int getShortValue(BigDecimal b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public static byte getByteValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public static byte getByteValue(Byte b, Byte defaultVal) {
        if (b == null) {
            return getByteValue(defaultVal);
        }
        return b.byteValue();
    }

    public static byte getByteValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public static byte getByteValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public static byte getByteValue(Long b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public static byte getByteValue(BigDecimal b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public static double getDoubleValue(Double b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public static double getDoubleValue(Double b, Double defaultVal) {
        if (b == null) {
            return getDoubleValue(defaultVal);
        }
        return b.doubleValue();
    }

    public static double getDoubleValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public static double getDoubleValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public static double getDoubleValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public static double getDoubleValue(Long b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public static double getDoubleValue(BigDecimal b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public static BigDecimal getBiDecimal(Byte s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public static BigDecimal getBiDecimal(Short s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public static BigDecimal getBiDecimal(Integer s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public static BigDecimal getBiDecimal(Long s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public static BigDecimal getBiDecimal(Double s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public static BigDecimal getBiDecimal(String s) {
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        return new BigDecimal(s);
    }

    /***************************************
     * Primitive 2 String
     *************************************/

    public static String getString(Byte num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public static String getString(Short num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public static String getString(Integer num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public static String getString(Long num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public static String getString(Float num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public static String getString(Double num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    /***************************************
     * String 2 Primitive
     *************************************/

    /**
     * {@link MwMoreNumberUtil#toByte(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public static byte getByteValue(String str) {
        return getByteValue(str, (byte) 0);
    }

    /**
     * {@link MwMoreNumberUtil#toByte(String, byte)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public static byte getByteValue(String str, byte defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Byte.valueOf(str);
    }

    /**
     * {@link MwMoreNumberUtil#toShort(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public static short getShortValue(String str) {
        return getShortValue(str, (short) 0);
    }

    /**
     * {@link MwMoreNumberUtil#toShort(String, short)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public static short getShortValue(String str, short defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Short.valueOf(str).shortValue();
    }

    /**
     * {@link MwMoreNumberUtil#toInt(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public static int getIntValue(String str) {
        return getIntValue(str, 0);
    }

    /**
     * {@link MwMoreNumberUtil#toInt(String, int)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public static int getIntValue(String str, int defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Integer.valueOf(str).intValue();
    }

    /**
     * {@link MwMoreNumberUtil#toLong(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public static long getLongValue(String str) {
        return getLongValue(str, 0L);
    }

    /**
     * {@link MwMoreNumberUtil#toLong(String, long)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public static long getLongValue(String str, long defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Long.valueOf(str).longValue();
    }

    /**
     * {@link MwMoreNumberUtil#toDouble(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public static double getDoubleValue(String str) {
        return getDoubleValue(str, 0);
    }

    /**
     * {@link MwMoreNumberUtil#toDouble(String, double)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public static double getDoubleValue(String str, double defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Double.valueOf(str).doubleValue();
    }

    public static double getDoubleValue(double value, int length) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(length, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /***************************************常见的四舍五入*************************************/

    /**
     * 经典的四舍五入, 对应php的round
     *
     * @param value
     * @param scale
     * @return
     */
    public static double round(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 舍弃小数部分, 对应php的floor
     *
     * @param value
     * @return
     */
    public static int floor(double value) {
        return common(value, RoundingMode.DOWN);
    }

    /**
     * 入小数部分, 对应php的ceil
     *
     * @param value
     * @return
     */
    public static int ceil(double value) {
        return common(value, RoundingMode.UP);
    }

    private static int common(double value, RoundingMode mode) {
        return BigDecimal.valueOf(value).setScale(0, mode).intValue();
    }
}
