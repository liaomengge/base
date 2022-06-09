package com.github.liaomengge.base_common.utils.number;


import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public class LyNumberUtil {

    public <T extends Number> long getLongValue(T t) {
        return getLongValue(t, NumberUtils.LONG_ZERO);
    }

    public <T extends Number> long getLongValue(T t, Long defaultVal) {
        return Optional.ofNullable(t).map(T::longValue).orElse(defaultVal);
    }

    public <T extends Number> int getIntValue(T t) {
        return getIntValue(t, NumberUtils.INTEGER_ZERO);
    }

    public <T extends Number> int getIntValue(T t, Integer defaultVal) {
        return Optional.ofNullable(t).map(T::intValue).orElse(defaultVal);
    }

    public <T extends Number> short getShortValue(T t) {
        return getShortValue(t, NumberUtils.SHORT_ZERO);
    }

    public <T extends Number> short getShortValue(T t, Short defaultVal) {
        return Optional.ofNullable(t).map(T::shortValue).orElse(defaultVal);
    }

    public <T extends Number> byte getByteValue(T t) {
        return getByteValue(t, NumberUtils.BYTE_ZERO);
    }

    public <T extends Number> byte getByteValue(T t, Byte defaultVal) {
        return Optional.ofNullable(t).map(T::byteValue).orElse(defaultVal);
    }

    public <T extends Number> double getDoubleValue(T t) {
        return getDoubleValue(t, NumberUtils.DOUBLE_ZERO);
    }

    public <T extends Number> double getDoubleValue(T t, Double defaultVal) {
        return Optional.ofNullable(t).map(T::doubleValue).orElse(defaultVal);
    }

    public <T extends Number> float getFloatValue(T t) {
        return getFloatValue(t, NumberUtils.FLOAT_ZERO);
    }

    public <T extends Number> float getFloatValue(T t, Float defaultVal) {
        return Optional.ofNullable(t).map(T::floatValue).orElse(defaultVal);
    }

    public BigDecimal getBiDecimal(Integer val) {
        if (val == null) {
            return null;
        }
        return new BigDecimal(val.toString());
    }

    public BigDecimal getBiDecimal(Long val) {
        if (val == null) {
            return null;
        }
        return new BigDecimal(val.toString());
    }

    public BigDecimal getBiDecimal(Short val) {
        if (val == null) {
            return null;
        }
        return new BigDecimal(val.toString());
    }

    public BigDecimal getBiDecimal(Byte val) {
        if (val == null) {
            return null;
        }
        return new BigDecimal(val.toString());
    }

    public BigDecimal getBiDecimal(Double val) {
        if (val == null) {
            return null;
        }
        return new BigDecimal(val.toString());
    }

    public BigDecimal getBiDecimal(Float val) {
        if (val == null) {
            return null;
        }
        return new BigDecimal(val.toString());
    }

    public BigDecimal getBiDecimal(String val) {
        if (StringUtils.isBlank(val)) {
            return null;
        }
        return new BigDecimal(val);
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
}
