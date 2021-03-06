package com.github.liaomengge.base_common.utils.encode;

import com.github.liaomengge.base_common.support.misc.Encodings;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 16/11/29.
 */
@UtilityClass
public class LyURLEncoder {

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
