package cn.mwee.base_common.utils.codec;

import cn.mwee.base_common.support.misc.Encodings;

import java.io.UnsupportedEncodingException;

/**
 * Created by liaomengge on 16/10/17.
 */
public final class MwCharsetUtil {

    private MwCharsetUtil() {
    }

    public static String UTF2GBK(String value) {
        try {
            return new String(value.getBytes(), Encodings.GBK);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public static String GBK2UTF(String value) {
        try {
            return new String(value.getBytes(), Encodings.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
