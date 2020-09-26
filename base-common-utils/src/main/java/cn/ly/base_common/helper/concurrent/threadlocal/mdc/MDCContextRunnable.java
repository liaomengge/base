package cn.ly.base_common.helper.concurrent.threadlocal.mdc;

import cn.ly.base_common.helper.concurrent.threadlocal.ThreadLocalRunnable;

import java.util.Map;

import org.slf4j.MDC;

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
    public void set(Map<String, String> contextMap) {
        MDC.setContextMap(contextMap);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    public static MDCContextRunnable wrapRunnable(Runnable runnable) {
        return new MDCContextRunnable(runnable, MDC.getCopyOfContextMap());
    }
}
