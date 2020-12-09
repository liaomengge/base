package com.github.liaomengge.base_common.helper.concurrent.threadlocal.multi;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalCallable;
import com.github.liaomengge.base_common.helper.concurrent.threadlocal.multi.pojo.MultiContextInfo;
import com.github.liaomengge.base_common.support.threadlocal.ThreadLocalContextUtils;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Created by liaomengge on 2020/12/8.
 */
public class MultiContextCallable<V> extends ThreadLocalCallable<MultiContextInfo, V> {

    public MultiContextCallable(Callable<V> delegate) {
        super(delegate);
    }

    public MultiContextCallable(Callable<V> delegate, MultiContextInfo context) {
        super(delegate, context);
    }

    @Override
    public void set(MultiContextInfo multiContextInfo) {
        Map<String, String> mdcContext = multiContextInfo.getMdcContext();
        Optional.ofNullable(mdcContext).ifPresent(MDC::setContextMap);
        Map<String, Object> mapContext = multiContextInfo.getMapContext();
        Optional.ofNullable(mapContext).ifPresent(ThreadLocalContextUtils::putAll);
        RequestAttributes requestAttributesContext = multiContextInfo.getRequestAttributesContext();
        Optional.ofNullable(requestAttributesContext).ifPresent(RequestContextHolder::setRequestAttributes);
    }

    @Override
    public void clear() {
        MDC.clear();
        ThreadLocalContextUtils.remove();
        RequestContextHolder.resetRequestAttributes();
    }

    public static <V> MultiContextCallable<V> wrapCallable(Callable<V> callable) {
        if (callable instanceof MultiContextCallable) {
            return (MultiContextCallable<V>) callable;
        }
        MultiContextInfo multiContextInfo = new MultiContextInfo();
        multiContextInfo.setMdcContext(MDC.getCopyOfContextMap());
        multiContextInfo.setMapContext(ThreadLocalContextUtils.getAll());
        multiContextInfo.setRequestAttributesContext(RequestContextHolder.getRequestAttributes());
        return new MultiContextCallable(callable, multiContextInfo);
    }
}
