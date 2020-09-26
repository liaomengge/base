package cn.ly.base_common.utils.codec;

import org.apache.commons.codec.digest.DigestUtils;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 16/9/12.
 */
@UtilityClass
public class LyDigestUtil {

    public String sha1(String str) {
        return DigestUtils.sha1Hex(str);
    }

    public String sha1(byte[] bytes) {
        return DigestUtils.sha1Hex(bytes);
    }

    public String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public String md5(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }
}
