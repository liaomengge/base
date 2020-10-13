package com.github.liaomengge.base_common.support.function.checked;

/**
 * Checked Supplier
 * <p>
 * Created by liaomengge on 2019/10/15.
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

    /**
     * Run the Supplier
     *
     * @return T
     * @throws Throwable CheckedException
     */
    T get() throws Throwable;

}
