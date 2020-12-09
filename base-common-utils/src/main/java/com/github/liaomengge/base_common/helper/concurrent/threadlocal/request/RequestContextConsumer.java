package com.github.liaomengge.base_common.helper.concurrent.threadlocal.request;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalConsumer;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.function.Consumer;

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
    public void set(RequestAttributes requestAttributesContext) {
        RequestContextHolder.setRequestAttributes(requestAttributesContext);
    }

    @Override
    public void clear() {
        RequestContextHolder.resetRequestAttributes();
    }

    public static <V> RequestContextConsumer<V> wrapConsumer(Consumer<V> consumer) {
        if (consumer instanceof RequestContextConsumer) {
            return (RequestContextConsumer<V>) consumer;
        }
        return new RequestContextConsumer(consumer, RequestContextHolder.getRequestAttributes());
    }
}
