package com.github.liaomengge.base_common.support.function.checked;

/**
 * Checked Comparator
 * <p>
 * Created by liaomengge on 2019/10/15.
 */
@FunctionalInterface
public interface CheckedComparator<T> {

    /**
     * Compares its two arguments for order.
     *
     * @param o1 o1
     * @param o2 o2
     * @return int
     * @throws Throwable CheckedException
     */
    int compare(T o1, T o2) throws Throwable;

}
