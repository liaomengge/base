package com.github.liaomengge.base_common.utils.number;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/11/20.
 */
@UtilityClass
public class LyPowerUtil {

    private final int MAXIMUM_CAPACITY = 1 << 30;

    public int two(int num) {
        int n = num - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
}
