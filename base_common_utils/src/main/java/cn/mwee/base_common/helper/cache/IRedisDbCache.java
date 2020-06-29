package cn.mwee.base_common.helper.cache;

/**
 * Created by liaomengge on 16/12/12.
 */
@Deprecated
@FunctionalInterface
public interface IRedisDbCache<T> {

    T handle();
}
