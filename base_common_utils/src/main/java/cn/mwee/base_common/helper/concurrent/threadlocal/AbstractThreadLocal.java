package cn.mwee.base_common.helper.concurrent.threadlocal;

/**
 * Created by liaomengge on 2020/5/20.
 */
public abstract class AbstractThreadLocal<T> {

    public abstract void set(T t);

    public abstract void clear();
}
