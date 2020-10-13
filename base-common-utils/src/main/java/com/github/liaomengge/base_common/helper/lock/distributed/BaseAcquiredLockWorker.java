package com.github.liaomengge.base_common.helper.lock.distributed;

/**
 * Created by liaomengge on 17/12/21.
 */
public abstract class BaseAcquiredLockWorker<T> implements AcquiredLockWorker<T> {

    @Override
    public T lockFail() {
        return null;
    }
}
