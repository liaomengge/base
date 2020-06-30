package cn.ly.base_common.support.function.checked;


/**
 * Checked Callable
 * <p>
 * Created by liaomengge on 2019/10/15.
 */
@FunctionalInterface
public interface CheckedCallable<T> {

    /**
     * Run this callable.
     *
     * @return result
     * @throws Throwable CheckedException
     */
    T call() throws Throwable;
}
