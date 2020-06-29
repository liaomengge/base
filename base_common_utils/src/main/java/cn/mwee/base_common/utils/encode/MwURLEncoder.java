package cn.mwee.base_common.utils.encode;

import cn.mwee.base_common.support.misc.Encodings;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by liaomengge on 16/11/29.
 */
public final class MwURLEncoder {

    private MwURLEncoder() {
    }

    public static String encode(String url) {
        return encode(url, Encodings.UTF_8);
    }

    public static String encode(String url, String charsetName) {
        try {
            return URLEncoder.encode(url, charsetName);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    public static String decode(String url) {
        return decode(url, Encodings.UTF_8);
    }

    public static String decode(String url, String charsetName) {
        try {
            return URLDecoder.decode(url, charsetName);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }
}
