package com.github.liaomengge.base_common.helper.concurrent.threadlocal.request;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalSupplier;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.function.Supplier;

/**
 * Created by liaomengge on 2020/5/26.
 */
public class RequestContextSupplier<V> extends ThreadLocalSupplier<RequestAttributes, V> {

    public RequestContextSupplier(Supplier<V> delegate) {
        super(delegate);
    }

    public RequestContextSupplier(Supplier<V> delegate, RequestAttributes context) {
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

    public static <V> RequestContextSupplier<V> wrapSupplier(Supplier<V> supplier) {
        if (supplier instanceof RequestContextSupplier) {
            return (RequestContextSupplier<V>) supplier;
        }
        return new RequestContextSupplier(supplier, RequestContextHolder.getRequestAttributes());
    }
}
