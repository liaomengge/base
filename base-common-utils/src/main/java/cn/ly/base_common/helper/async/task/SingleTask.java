package cn.ly.base_common.helper.async.task;

import cn.ly.base_common.helper.async.callback.BaseFutureCallback;

import java.util.concurrent.Callable;

/**
 * Created by liaomengge on 17/7/21.
 */
public class SingleTask<P, V> implements Callable<V> {

    private P param;
    private BaseFutureCallback<P, V> baseFutureCallback;

    public SingleTask(P param, BaseFutureCallback<P, V> baseFutureCallback) {
        this.param = param;
        this.baseFutureCallback = baseFutureCallback;
    }

    @Override
    public V call() throws Exception {
        return baseFutureCallback.execute(param);
    }
}
