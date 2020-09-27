package cn.ly.base_common.utils.codec;

import cn.ly.base_common.support.misc.Encodings;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by liaomengge on 16/9/6.
 */
@UtilityClass
public class LyBase64Util {

    public String encode(byte[] binaryData) {
        return encode(binaryData, Encodings.UTF_8);
    }

    public String decode(String base64String) {
        return decode(base64String, Encodings.UTF_8);
    }

    public String encode(byte[] binaryData, String charsetName) {
        try {
            return new String(Base64.encodeBase64(binaryData), charsetName);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public String decode(String base64String, String charsetName) {
        try {
            return new String(Base64.decodeBase64(base64String.getBytes(charsetName)), charsetName);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
