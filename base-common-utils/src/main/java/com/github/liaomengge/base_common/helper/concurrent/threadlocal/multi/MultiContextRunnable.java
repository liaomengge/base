package com.github.liaomengge.base_common.helper.concurrent.threadlocal.multi;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalRunnable;
import com.github.liaomengge.base_common.helper.concurrent.threadlocal.multi.pojo.MultiContextInfo;
import com.github.liaomengge.base_common.support.threadlocal.ThreadLocalContextUtils;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.Optional;

/**
 * Created by liaomengge on 2020/12/8.
 */
public class MultiContextRunnable extends ThreadLocalRunnable<MultiContextInfo> {

    public MultiContextRunnable(Runnable delegate) {
        super(delegate);
    }

    public MultiContextRunnable(Runnable delegate, MultiContextInfo context) {
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

    public static MultiContextRunnable wrapRunnable(Runnable runnable) {
        if (runnable instanceof MultiContextRunnable) {
            return (MultiContextRunnable) runnable;
        }
        MultiContextInfo multiContextInfo = new MultiContextInfo();
        multiContextInfo.setMdcContext(MDC.getCopyOfContextMap());
        multiContextInfo.setMapContext(ThreadLocalContextUtils.getAll());
        multiContextInfo.setRequestAttributesContext(RequestContextHolder.getRequestAttributes());
        return new MultiContextRunnable(runnable, multiContextInfo);
    }
}
