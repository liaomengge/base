package cn.ly.base_common.utils.random;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by liaomengge on 17/11/26.
 */
@UtilityClass
public class MwRandomUtil {

    public Random random() {
        return new Random();
    }

    public Random random(long seed) {
        return new Random(seed);
    }

    public ThreadLocalRandom threadLocalRandom() {
        return ThreadLocalRandom.current();
    }

    ////////////////// nextInt 相关/////////

    /**
     * 返回0到Intger.MAX_VALUE的随机Int, 使用内置全局普通Random.
     */
    public int nextInt() {
        return nextInt(threadLocalRandom());
    }

    /**
     * 返回0到Intger.MAX_VALUE的随机Int, 可传入SecureRandom或ThreadLocalRandom
     */
    public int nextInt(Random random) {
        int n = random.nextInt();
        if (n == Integer.MIN_VALUE) {
            n = 0; // corner case
        } else {
            n = Math.abs(n);
        }

        return n;
    }

    /**
     * 返回0到max的随机Int, 使用内置全局普通Random.
     */
    public int nextInt(int max) {
        return nextInt(threadLocalRandom(), max);
    }

    /**
     * 返回0到max的随机Int, 可传入SecureRandom或ThreadLocalRandom
     */
    public int nextInt(Random random, int max) {
        return random.nextInt(max);
    }

    /**
     * 返回min到max的随机Int, 使用内置全局普通Random.
     * <p>
     * min必须大于0.
     */
    public int nextInt(int min, int max) {
        return nextInt(threadLocalRandom(), min, max);
    }

    /**
     * 返回min到max的随机Int,可传入SecureRandom或ThreadLocalRandom.
     * <p>
     * min必须大于0.
     * <p>
     * JDK本身不具有控制两端范围的nextInt, 因此参考Commons Lang RandomUtils的实现, 不直接复用是因为要传入Random实例
     */
    public int nextInt(Random random, int min, int max) {
        Validate.isTrue(max >= min, "Start value must be smaller or equal to end value.");
        if (min == max) {
            return min;
        }

        return min + random.nextInt(max - min);
    }

    ////////////////// long 相关/////////

    /**
     * 返回0－Long.MAX_VALUE间的随机Long, 使用内置全局普通Random.
     */
    public long nextLong() {
        return nextLong(threadLocalRandom());
    }

    /**
     * 返回0－Long.MAX_VALUE间的随机Long, 可传入SecureRandom或ThreadLocalRandom
     */
    public long nextLong(Random random) {
        long n = random.nextLong();
        if (n == Long.MIN_VALUE) {
            n = 0; // corner case
        } else {
            n = Math.abs(n);
        }
        return n;
    }

    /**
     * 返回0－max间的随机Long, 使用内置全局普通Random.
     */
    public long nextLong(long max) {
        return nextLong(threadLocalRandom(), 0, max);
    }

    /**
     * 返回0-max间的随机Long, 可传入SecureRandom或ThreadLocalRandom
     */
    public long nextLong(Random random, long max) {
        return nextLong(random, 0, max);
    }

    /**
     * 返回min－max间的随机Long, 使用内置全局普通Random.
     * <p>
     * min必须大于0.
     */
    public long nextLong(long min, long max) {
        return nextLong(threadLocalRandom(), min, max);
    }

    /**
     * 返回min-max间的随机Long,可传入SecureRandom或ThreadLocalRandom.
     * <p>
     * min必须大于0.
     * <p>
     * JDK本身不具有控制两端范围的nextLong, 因此参考Commons Lang RandomUtils的实现, 不直接复用是因为要传入Random实例
     *
     * @see org.apache.commons.lang3.RandomUtils#nextLong(long, long)
     */
    public long nextLong(Random random, long min, long max) {
        Validate.isTrue(max >= min, "Start value must be smaller or equal to end value.");
        if (min == max) {
            return min;
        }

        return (long) (min + ((max - min) * random.nextDouble()));
    }

    ///////// Double //////

    /**
     * 返回0-之间的double
     */
    public double nextDouble() {
        return nextDouble(threadLocalRandom(), 0, Double.MAX_VALUE);
    }

    /**
     * 返回0-Double.MAX之间的double
     */
    public double nextDouble(Random random) {
        return nextDouble(random, 0, Double.MAX_VALUE);
    }

    /**
     * 返回0-max之间的double
     * <p>
     * 注意：与JDK默认返回0-1的行为不一致.
     */
    public double nextDouble(double max) {
        return nextDouble(threadLocalRandom(), 0, max);
    }

    /**
     * 返回0-max之间的double
     */
    public double nextDouble(Random random, double max) {
        return nextDouble(random, 0, max);
    }

    /**
     * 返回min-max之间的double
     */
    public double nextDouble(double min, double max) {
        return nextDouble(threadLocalRandom(), min, max);
    }

    /**
     * 返回min-max之间的double
     */
    public double nextDouble(Random random, double min, double max) {
        Validate.isTrue(max >= min, "Start value must be smaller or equal to end value.");
        if (min == max) {
            return min;
        }

        return min + ((max - min) * random.nextDouble());
    }
    //////////////////// String/////////

    /**
     * 随机字母或数字, 固定长度
     */
    public String randomStringFixLength(int length) {
        return RandomStringUtils.random(length, 0, 0, true, true, null, threadLocalRandom());
    }

    /**
     * 随机字母或数字, 固定长度
     */
    public String randomStringFixLength(Random random, int length) {
        return RandomStringUtils.random(length, 0, 0, true, true, null, random);
    }

    /**
     * 随机字母或数字, 随机长度
     */
    public String randomStringRandomLength(int minLength, int maxLength) {
        return RandomStringUtils.random(nextInt(minLength, maxLength), 0, 0, true, true, null, threadLocalRandom());
    }

    /**
     * 随机字母或数字, 随机长度
     */
    public String randomStringRandomLength(Random random, int minLength, int maxLength) {
        return RandomStringUtils.random(nextInt(random, minLength, maxLength), 0, 0, true, true, null, random);
    }

    /**
     * 随机字母, 固定长度
     */
    public String randomLetterFixLength(int length) {
        return RandomStringUtils.random(length, 0, 0, true, false, null, threadLocalRandom());
    }

    /**
     * 随机字母, 固定长度
     */
    public String randomLetterFixLength(Random random, int length) {
        return RandomStringUtils.random(length, 0, 0, true, false, null, random);
    }

    /**
     * 随机字母, 随机长度
     */
    public String randomLetterRandomLength(int minLength, int maxLength) {
        return RandomStringUtils.random(nextInt(minLength, maxLength), 0, 0, true, false, null, threadLocalRandom());
    }

    /**
     * 随机字母, 随机长度
     */
    public String randomLetterRandomLength(Random random, int minLength, int maxLength) {
        return RandomStringUtils.random(nextInt(random, minLength, maxLength), 0, 0, true, false, null, random);
    }

    /**
     * 随机ASCII字符(含字母, 数字及其他符号), 固定长度
     */
    public String randomAsciiFixLength(int length) {
        return RandomStringUtils.random(length, 32, 127, false, false, null, threadLocalRandom());
    }

    /**
     * 随机ASCII字符(含字母, 数字及其他符号), 固定长度
     */
    public String randomAsciiFixLength(Random random, int length) {
        return RandomStringUtils.random(length, 32, 127, false, false, null, random);
    }

    /**
     * 随机ASCII字符(含字母, 数字及其他符号), 随机长度
     */
    public String randomAsciiRandomLength(int minLength, int maxLength) {
        return RandomStringUtils.random(nextInt(minLength, maxLength), 32, 127, false, false, null,
                threadLocalRandom());
    }

    /**
     * 随机ASCII字符(含字母, 数字及其他符号), 随机长度
     */
    public String randomAsciiRandomLength(Random random, int minLength, int maxLength) {
        return RandomStringUtils.random(nextInt(random, minLength, maxLength), 32, 127, false, false, null, random);
    }
}
