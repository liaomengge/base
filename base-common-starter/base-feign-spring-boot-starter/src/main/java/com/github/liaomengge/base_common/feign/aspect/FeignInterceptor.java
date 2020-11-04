package com.github.liaomengge.base_common.feign.aspect;

import com.github.liaomengge.base_common.feign.FeignProperties;
import com.github.liaomengge.base_common.feign.pojo.FeignLogInfo;
import com.github.liaomengge.base_common.feign.util.FeignLogUtil;
import com.github.liaomengge.base_common.support.threadlocal.ThreadLocalContextUtils;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.log.LyMDCUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.springframework.core.NamedThreadLocal;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.liaomengge.base_common.feign.consts.FeignConst.FEIGN_LOG_INFO_THREAD_CONTEXT;

/**
 * Created by liaomengge on 2020/10/31.
 */
public class FeignInterceptor implements MethodInterceptor, RequestInterceptor {

    private static final Logger log = LyLogger.getInstance(FeignInterceptor.class);

    private static ThreadLocal<Map<String, Object>> feignLogInfoThreadContextMap =
            new NamedThreadLocal("FEIGN_LOG_INFO_THREAD_CONTEXT_MAP");

    private final FeignProperties feignProperties;

    public FeignInterceptor(FeignProperties feignProperties) {
        this.feignProperties = feignProperties;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        FeignLogInfo logInfo = new FeignLogInfo();
        long startNanoTime = System.nanoTime();
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();
        String classMethod = '[' + method.getDeclaringClass().getSimpleName() + '#' + method.getName() + ']';
        logInfo.setClassMethod(classMethod);
        if (!FeignLogUtil.isIgnoreLogRequest(method.getName(), feignProperties)) {
            logInfo.setRequestBody(LyWebUtil.getRequestParams(method, args));
        }
        boolean isSuccess = false;
        Object result = null;
        try {
            result = invocation.proceed();
            isSuccess = true;
        } catch (Throwable t) {
            logInfo.setExceptionStackTrace(LyThrowableUtil.getStackTrace(t));
            throw t;
        } finally {
            try {
                FeignLogInfo threadLocalLogInfo = ThreadLocalContextUtils.get(feignLogInfoThreadContextMap,
                        FEIGN_LOG_INFO_THREAD_CONTEXT);
                logInfo.setUrl(threadLocalLogInfo.getUrl());
                logInfo.setHttpMethod(threadLocalLogInfo.getHttpMethod());
                if (!FeignLogUtil.isIgnoreLogHeader(method.getName(), feignProperties)) {
                    logInfo.setHeaderParams(threadLocalLogInfo.getHeaderParams());
                }
                if (!FeignLogUtil.isIgnoreLogResponse(method.getName(), feignProperties)) {
                    logInfo.setResponseBody(result);
                    logInfo.setQueryParams(threadLocalLogInfo.getQueryParams());
                }
                long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanoTime);
                logInfo.setElapsedTime(elapsedTime);
                LyMDCUtil.put(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME, String.valueOf(elapsedTime));
                if (isSuccess) {
                    log.info("request response log info => {}", LyJsonUtil.toJson4Log(logInfo));
                } else {
                    log.error("request response log info => {}", LyJsonUtil.toJson4Log(logInfo));
                }
            } finally {
                LyMDCUtil.remove(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME);
                ThreadLocalContextUtils.remove(feignLogInfoThreadContextMap);
            }
        }
        return result;
    }

    @Override
    public void apply(RequestTemplate template) {
        FeignLogInfo feignLogInfo = new FeignLogInfo();
        feignLogInfo.setUrl(template.url());
        feignLogInfo.setHttpMethod(template.method());
        feignLogInfo.setQueryParams(template.queries());
        template
        if (!FeignLogUtil.isIgnoreLogHeader(method.getName(), feignProperties)) {
            feignLogInfo.setHeaderParams(template.headers());
        }
        ThreadLocalContextUtils.put(feignLogInfoThreadContextMap, FEIGN_LOG_INFO_THREAD_CONTEXT, feignLogInfo);
    }
}
