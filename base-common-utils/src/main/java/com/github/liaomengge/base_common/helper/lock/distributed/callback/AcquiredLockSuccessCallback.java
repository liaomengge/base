package com.github.liaomengge.base_common.helper.lock.distributed.callback;

/**
 * Created by liaomengge on 2020/10/30.
 */
public interface AcquiredLockSuccessCallback<T> {

    default T onSuccess() {
        return null;
    }
}
