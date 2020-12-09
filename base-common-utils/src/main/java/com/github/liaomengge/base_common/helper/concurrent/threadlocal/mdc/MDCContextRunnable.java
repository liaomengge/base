package com.github.liaomengge.base_common.helper.concurrent.threadlocal.mdc;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalRunnable;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Created by liaomengge on 2020/5/20.
 */
public class MDCContextRunnable extends ThreadLocalRunnable<Map<String, String>> {

    public MDCContextRunnable(Runnable delegate) {
        super(delegate);
    }

    public MDCContextRunnable(Runnable delegate, Map<String, String> context) {
        super(delegate, context);
    }

    @Override
    public void set(Map<String, String> mdcContext) {
        MDC.setContextMap(mdcContext);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    public static MDCContextRunnable wrapRunnable(Runnable runnable) {
        if (runnable instanceof MDCContextRunnable) {
            return (MDCContextRunnable) runnable;
        }
        return new MDCContextRunnable(runnable, MDC.getCopyOfContextMap());
    }
}
