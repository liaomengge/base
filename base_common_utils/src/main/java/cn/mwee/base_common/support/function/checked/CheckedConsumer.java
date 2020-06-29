package cn.mwee.base_common.support.function.checked;

/**
 * Checked Consumer
 * <p>
 * Created by liaomengge on 2019/10/15.
 */
@FunctionalInterface
public interface CheckedConsumer<T> {

    /**
     * Run the Consumer
     *
     * @param t T
     * @throws Throwable UncheckedException
     */
    void accept(T t) throws Throwable;

}
