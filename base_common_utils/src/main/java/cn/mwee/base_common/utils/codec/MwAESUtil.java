package cn.mwee.base_common.utils.codec;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by liaomengge on 17/2/23.
 */
@UtilityClass
public class MwAESUtil {

    private final byte[] LINE_SEPARATOR = {};

    //密钥长度
    private final int KEY_SIZE = 128;

    //密钥算法
    private final String KEY_ALGORITHM = "AES";

    //加解密算法/工作模式/填充方式,Java6.0支持PKCS5Padding填充方式,BouncyCastle支持PKCS7Padding填充方式
    private final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private final Base64 base64;

    static {
        base64 = new Base64(KEY_SIZE, LINE_SEPARATOR, true);
    }

    /**
     * 点评版, 加密数据
     *
     * @param key  密钥
     * @param data 待加密数据
     * @return 加密后的数据
     */
    public String encrypt(String key, String data) throws Exception {
        Key k = toKey(base64.decode(key));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "SunJCE");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] cipherText = cipher.doFinal(data.getBytes());
        return new String(base64.encode(cipherText));
    }

    /**
     * 点评版, 解密数据
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public String decrypt(String key, String data) throws Exception {
        Key k = toKey(base64.decode(key));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "SunJCE");
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] decrypted = cipher.doFinal(base64.decode(data.getBytes()));
        return new String(decrypted, "UTF-8");
    }

    /**
     * php 2 java版, 加密数据
     *
     * @param key  密钥
     * @param data 待加密数据
     * @return 加密后的数据
     */
    public String encrypt2(String key, String data) throws Exception {
        Key k = toKey(key.getBytes());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "SunJCE");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] cipherText = cipher.doFinal(data.getBytes());
        return new String(base64.encode(cipherText));
    }

    /**
     * php 2 java版, 解密数据
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public String decrypt2(String key, String data) throws Exception {
        Key k = toKey(key.getBytes());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "SunJCE");
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] decrypted = cipher.doFinal(base64.decode(data.getBytes()));
        return new String(decrypted, "UTF-8");
    }

    /**
     * 转换密钥
     */
    private Key toKey(byte[] key) throws Exception {
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }
}
