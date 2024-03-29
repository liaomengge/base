package com.github.liaomengge.base_common.utils.random;

import com.github.liaomengge.base_common.utils.number.LyNumberUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaomengge on 2019/11/20.
 */
@Slf4j
@UtilityClass
public class LyMoreRandomUtil {
    
    private final double DOUBLE = 3.14d;

    private final Map<Integer, Long> PRIME_NUMBER_MAP = new HashMap<Integer, Long>(24) {

        private static final long serialVersionUID = 2761764002048315558L;

        {
            put(1, Long.valueOf("7"));
            put(2, Long.valueOf("97"));
            put(3, Long.valueOf("997"));
            put(4, Long.valueOf("9973"));
            put(5, Long.valueOf("99991"));
            put(6, Long.valueOf("999983"));
            put(7, Long.valueOf("9999991"));
            put(8, Long.valueOf("99999989"));
            put(9, Long.valueOf("999999937"));
            put(10, Long.valueOf("9999999967"));
            put(11, Long.valueOf("99999999977"));
            put(12, Long.valueOf("999999999989"));
            put(13, Long.valueOf("9999999999971"));
            put(14, Long.valueOf("99999999999973"));
            put(15, Long.valueOf("999999999999989"));
            put(16, Long.valueOf("9999999999999937"));
        }
    };

    /**
     * 按指定长度prime数, 生成随机数
     * 可以依据几个随机生成的做+发
     * eg:
     * pseudoRandom(2, 10) + pseudoRandom(3, 100)
     *
     * @param len
     * @param num
     * @return
     */
    public long pseudoRandom(int len, long num) {
        if (len <= 0 || num <= 0) {
            return 0;
        }
        long maxNumPrime = LyNumberUtil.getLongValue(PRIME_NUMBER_MAP.get(len));
        if (maxNumPrime == 0) {
            throw new IndexOutOfBoundsException("len[" + len + "]>" + PRIME_NUMBER_MAP.size() + ", over max limit");
        }
        if (num >= maxNumPrime) {
            log.warn("num[{}]>={}, over max limit, maybe generator repeated random number", num, maxNumPrime);
        }
        long seed = Math.round(maxNumPrime / DOUBLE);
        return (seed * (num + seed)) % maxNumPrime;
    }
}
