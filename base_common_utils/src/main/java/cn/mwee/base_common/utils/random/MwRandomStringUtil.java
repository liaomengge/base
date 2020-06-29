package cn.mwee.base_common.utils.random;

import java.util.Random;

/**
 * Created by liaomengge on 17/6/27.
 */
@Deprecated
public final class MwRandomStringUtil {

    private MwRandomStringUtil() {
    }

    private static Random random = MwRandomUtil.threadLocalRandom();
    private static Random random2 = MwRandomUtil.random(System.currentTimeMillis());

    public static Random getRandomSed() {
        return random;
    }

    public static Random getRandomSed2() {
        return random2;
    }

    /**
     * 可能生成重复的16位随机码
     *
     * @return
     */
    public static String generateDefaultRandomSed() {
        return IdConversion.convertToString(random.nextLong());
    }

    /**
     * 生成16位随机码
     *
     * @return
     */
    public static String generateDefaultRandomSed2() {
        return IdConversion.convertToString(random2.nextLong());
    }

    public static String generateRandomSed(String str) {
        return str + "_" + IdConversion.convertToString(random.nextLong());
    }

    public static String generateRandomSed2(String str) {
        return str + "_" + IdConversion.convertToString(random2.nextLong());
    }

    public static class IdConversion {
        public static String convertToString(long id) {
            return Long.toHexString(id);
        }

        public static long convertToLong(String lowerHex) {
            int length = lowerHex.length();
            if (length >= 1 && length <= 32) {
                int beginIndex = length > 16 ? length - 16 : 0;
                return convertToLong(lowerHex, beginIndex);
            } else {
                throw isNotLowerHexLong(lowerHex);
            }
        }

        public static long convertToLong(String lowerHex, int index) {
            long result = 0L;

            for (int endIndex = Math.min(index + 16, lowerHex.length()); index < endIndex; ++index) {
                char c = lowerHex.charAt(index);
                result <<= 4;
                if (c >= 48 && c <= 57) {
                    result |= (long) (c - 48);
                } else {
                    if (c < 97 || c > 102) {
                        throw isNotLowerHexLong(lowerHex);
                    }

                    result |= (long) (c - 97 + 10);
                }
            }

            return result;
        }

        static NumberFormatException isNotLowerHexLong(String lowerHex) {
            throw new NumberFormatException(lowerHex + " should be a 1 to 32 character lower-hex string with no prefix");
        }
    }
}
