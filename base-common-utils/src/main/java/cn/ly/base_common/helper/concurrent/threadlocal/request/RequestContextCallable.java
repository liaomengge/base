package cn.ly.base_common.helper.concurrent.threadlocal.request;

import cn.ly.base_common.helper.concurrent.threadlocal.ThreadLocalCallable;

import java.util.concurrent.Callable;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Created by liaomengge on 2020/5/26.
 */
public class RequestContextCallable<V> extends ThreadLocalCallable<RequestAttributes, V> {

    public RequestContextCallable(Callable<V> delegate) {
        super(delegate);
    }

    public RequestContextCallable(Callable<V> delegate, RequestAttributes context) {
        super(delegate, context);
    }

    @Override
    public void set(RequestAttributes requestAttributes) {
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Override
    public void clear() {
        RequestContextHolder.resetRequestAttributes();
    }

    public static <V> Callable<V> wrapCallable(Callable<V> callable) {
        return new RequestContextCallable(callable, RequestContextHolder.getRequestAttributes());
    }
}
