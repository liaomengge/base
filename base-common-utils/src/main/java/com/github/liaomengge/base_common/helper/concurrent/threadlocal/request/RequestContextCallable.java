package com.github.liaomengge.base_common.helper.concurrent.threadlocal.request;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalCallable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

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
    public void set(RequestAttributes requestAttributesContext) {
        RequestContextHolder.setRequestAttributes(requestAttributesContext);
    }

    @Override
    public void clear() {
        RequestContextHolder.resetRequestAttributes();
    }

    public static <V> RequestContextCallable<V> wrapCallable(Callable<V> callable) {
        if (callable instanceof RequestContextCallable) {
            return (RequestContextCallable<V>) callable;
        }
        return new RequestContextCallable(callable, RequestContextHolder.getRequestAttributes());
    }
}
