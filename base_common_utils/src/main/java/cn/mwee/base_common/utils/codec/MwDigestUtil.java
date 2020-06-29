package cn.mwee.base_common.utils.codec;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by liaomengge on 16/9/12.
 */
public final class MwDigestUtil {

    private MwDigestUtil() {
    }

    public static String sha1(String str) {
        return DigestUtils.sha1Hex(str);
    }

    public static String sha1(byte[] bytes) {
        return DigestUtils.sha1Hex(bytes);
    }

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static String md5(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }
}
