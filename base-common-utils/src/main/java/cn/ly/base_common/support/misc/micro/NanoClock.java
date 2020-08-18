package cn.ly.base_common.support.misc.micro;

import java.io.Serializable;

/**
 * Created by liaomengge on 2020/8/18.
 */
public final class NanoClock implements Serializable {

    private static final long serialVersionUID = 526215658183123531L;
    
    private final long EPOCH_NANOS = System.currentTimeMillis() * 1_000_000;
    private final long NANO_START = System.nanoTime();
    private final long OFFSET_NANOS = EPOCH_NANOS - NANO_START;

    public long nanos() {
        return System.nanoTime() + OFFSET_NANOS;
    }
}
