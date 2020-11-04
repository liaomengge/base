package com.github.liaomengge.base_common.helper.lock.distributed.callback;

/**
 * Created by liaomengge on 17/12/19.
 */
public interface AcquiredLockCallback<T> extends AcquiredLockSuccessCallback<T>, AcquiredLockFailureCallback<T> {
}
