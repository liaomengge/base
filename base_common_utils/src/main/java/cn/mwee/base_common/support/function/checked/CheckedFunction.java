package cn.mwee.base_common.support.function.checked;

/**
 * Checked function
 * <p>
 * Created by liaomengge on 2019/10/15.
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {

    /**
     * Run the Function
     *
     * @param t T
     * @return R R
     * @throws Throwable CheckedException
     */
    R apply(T t) throws Throwable;

}
