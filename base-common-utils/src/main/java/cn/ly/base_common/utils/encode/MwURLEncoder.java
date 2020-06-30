package cn.ly.base_common.utils.encode;

import cn.ly.base_common.support.misc.Encodings;
import lombok.experimental.UtilityClass;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by liaomengge on 16/11/29.
 */
@UtilityClass
public class MwURLEncoder {

    public String encode(String url) {
        return encode(url, Encodings.UTF_8);
    }

    public String encode(String url, String charsetName) {
        try {
            return URLEncoder.encode(url, charsetName);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    public String decode(String url) {
        return decode(url, Encodings.UTF_8);
    }

    public String decode(String url, String charsetName) {
        try {
            return URLDecoder.decode(url, charsetName);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }
}
