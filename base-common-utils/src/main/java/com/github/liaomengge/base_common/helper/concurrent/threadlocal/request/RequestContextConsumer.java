package com.github.liaomengge.base_common.helper.concurrent.threadlocal.request;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalConsumer;

import java.util.function.Consumer;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Created by liaomengge on 2020/5/26.
 */
public class RequestContextConsumer<V> extends ThreadLocalConsumer<RequestAttributes, V> {

    public RequestContextConsumer(Consumer<V> delegate) {
        super(delegate);
    }

    public RequestContextConsumer(Consumer<V> delegate, RequestAttributes context) {
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

    public static <V> Consumer<V> wrapConsumer(Consumer<V> consumer) {
        return new RequestContextConsumer(consumer, RequestContextHolder.getRequestAttributes());
    }
}
