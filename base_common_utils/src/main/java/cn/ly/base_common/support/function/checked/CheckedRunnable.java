package cn.ly.base_common.support.function.checked;

/**
 * Checked runnable
 * <p>
 * Created by liaomengge on 2019/10/15.
 */
@FunctionalInterface
public interface CheckedRunnable {

    /**
     * Run this runnable.
     *
     * @throws Throwable CheckedException
     */
    void run() throws Throwable;

}
