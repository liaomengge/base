package cn.ly.base_common.utils.number;


import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LyNumberUtil {

    public long getLongValue(Long v) {
        if (v == null) {
            return 0;
        }
        return v.longValue();
    }

    public long getLongValue(Long v, Long defaultVal) {
        if (v == null) {
            return getLongValue(defaultVal);
        }
        return v.longValue();
    }

    public long getLongValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.longValue();
    }

    public long getLongValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.longValue();
    }

    public long getLongValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.longValue();
    }

    public long getLongValue(BigDecimal v) {
        if (v == null) {
            return 0;
        }
        return v.longValue();
    }

    public int getIntValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public int getIntValue(Integer b, Integer defaultVal) {
        if (b == null) {
            return getIntValue(defaultVal);
        }
        return b.intValue();
    }

    public int getIntValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public int getIntValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public int getIntValue(Long b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public int getIntValue(BigDecimal b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    public short getShortValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public double getShortValue(Short b, Short defaultVal) {
        if (b == null) {
            return getShortValue(defaultVal);
        }
        return b.doubleValue();
    }

    public short getShortValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public short getShortValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public short getShortValue(Long b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public int getShortValue(BigDecimal b) {
        if (b == null) {
            return 0;
        }
        return b.shortValue();
    }

    public byte getByteValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public byte getByteValue(Byte b, Byte defaultVal) {
        if (b == null) {
            return getByteValue(defaultVal);
        }
        return b.byteValue();
    }

    public byte getByteValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public byte getByteValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public byte getByteValue(Long b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public byte getByteValue(BigDecimal b) {
        if (b == null) {
            return 0;
        }
        return b.byteValue();
    }

    public double getDoubleValue(Double b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public double getDoubleValue(Double b, Double defaultVal) {
        if (b == null) {
            return getDoubleValue(defaultVal);
        }
        return b.doubleValue();
    }

    public double getDoubleValue(Byte b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public double getDoubleValue(Short b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public double getDoubleValue(Integer b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public double getDoubleValue(Long b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public double getDoubleValue(BigDecimal b) {
        if (b == null) {
            return 0;
        }
        return b.doubleValue();
    }

    public BigDecimal getBiDecimal(Byte s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public BigDecimal getBiDecimal(Short s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public BigDecimal getBiDecimal(Integer s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public BigDecimal getBiDecimal(Long s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public BigDecimal getBiDecimal(Double s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s.toString());
    }

    public BigDecimal getBiDecimal(String s) {
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        return new BigDecimal(s);
    }

    /***************************************
     * Primitive 2 String
     *************************************/

    public String getString(Byte num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public String getString(Short num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public String getString(Integer num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public String getString(Long num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public String getString(Float num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    public String getString(Double num) {
        if (num == null) {
            return "";
        }
        return num.toString();
    }

    /***************************************
     * String 2 Primitive
     *************************************/

    /**
     * {@link LyMoreNumberUtil#toByte(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public byte getByteValue(String str) {
        return getByteValue(str, (byte) 0);
    }

    /**
     * {@link LyMoreNumberUtil#toByte(String, byte)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public byte getByteValue(String str, byte defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Byte.valueOf(str);
    }

    /**
     * {@link LyMoreNumberUtil#toShort(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public short getShortValue(String str) {
        return getShortValue(str, (short) 0);
    }

    /**
     * {@link LyMoreNumberUtil#toShort(String, short)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public short getShortValue(String str, short defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Short.valueOf(str).shortValue();
    }

    /**
     * {@link LyMoreNumberUtil#toInt(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public int getIntValue(String str) {
        return getIntValue(str, 0);
    }

    /**
     * {@link LyMoreNumberUtil#toInt(String, int)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public int getIntValue(String str, int defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Integer.parseInt(str);
    }

    /**
     * {@link LyMoreNumberUtil#toLong(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public long getLongValue(String str) {
        return getLongValue(str, 0L);
    }

    /**
     * {@link LyMoreNumberUtil#toLong(String, long)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public long getLongValue(String str, long defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Long.parseLong(str);
    }

    /**
     * {@link LyMoreNumberUtil#toDouble(String)}
     *
     * @param str
     * @return
     */
    @Deprecated
    public double getDoubleValue(String str) {
        return getDoubleValue(str, 0);
    }

    /**
     * {@link LyMoreNumberUtil#toDouble(String, double)}
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @Deprecated
    public double getDoubleValue(String str, double defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return Double.valueOf(str).doubleValue();
    }

    public double getDoubleValue(double value, int length) {
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
    public double round(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 舍弃小数部分, 对应php的floor
     *
     * @param value
     * @return
     */
    public int floor(double value) {
        return common(value, RoundingMode.DOWN);
    }

    /**
     * 入小数部分, 对应php的ceil
     *
     * @param value
     * @return
     */
    public int ceil(double value) {
        return common(value, RoundingMode.UP);
    }

    private int common(double value, RoundingMode mode) {
        return BigDecimal.valueOf(value).setScale(0, mode).intValue();
    }
}
