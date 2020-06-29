package cn.mwee.base_common.helper.concurrent.threadlocal.request;

import cn.mwee.base_common.helper.concurrent.threadlocal.ThreadLocalRunnable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Created by liaomengge on 2020/5/26.
 */
public class RequestContextRunnable extends ThreadLocalRunnable<RequestAttributes> {

    public RequestContextRunnable(Runnable delegate) {
        super(delegate);
    }

    public RequestContextRunnable(Runnable delegate, RequestAttributes context) {
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

    public static RequestContextRunnable wrapRunnable(Runnable runnable) {
        return new RequestContextRunnable(runnable, RequestContextHolder.getRequestAttributes());
    }
}
