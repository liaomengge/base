package com.github.liaomengge.base_common.helper.async.callback;

/**
 * Created by liaomengge on 17/12/2.
 */
public interface BaseFutureCallback<P, V> {

    V execute(P param) throws Exception;

    void doSuccess(P param, V result);

    void doFailure(P param, Throwable throwable);
}
