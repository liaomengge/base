package com.github.liaomengge.base_common.helper.lock.distributed.callback;

/**
 * Created by liaomengge on 2020/10/30.
 */
public interface AcquiredLockFailureCallback<T> {

    default T onFailure(Throwable throwable) {
        return null;
    }

    default T onFailure() {
        return null;
    }
}
