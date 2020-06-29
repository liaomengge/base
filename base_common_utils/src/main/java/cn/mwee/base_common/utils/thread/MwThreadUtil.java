package cn.mwee.base_common.utils.thread;

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/6/3.
 */
@UtilityClass
public class MwThreadUtil {

    public void sleep(long timeoutMillis) {
        try {
            Thread.sleep(timeoutMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sleep(long timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
