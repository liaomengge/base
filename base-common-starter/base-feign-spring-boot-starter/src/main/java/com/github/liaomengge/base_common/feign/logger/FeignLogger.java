package com.github.liaomengge.base_common.feign.logger;

import com.github.liaomengge.base_common.feign.FeignProperties;
import com.github.liaomengge.base_common.feign.pojo.FeignLogInfo;
import com.github.liaomengge.base_common.feign.util.FeignLogUtil;
import com.github.liaomengge.base_common.support.logger.JsonLogger;
import com.github.liaomengge.base_common.support.misc.Symbols;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.mdc.LyMDCUtil;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import feign.Util;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;

/**
 * Created by liaomengge on 2020/10/29.
 * <p>
 * 1.获取不到ribbon具体指向的ip {@link com.netflix.client.IClient}
 */
public class FeignLogger extends feign.Logger {

    private final JsonLogger log;
    private FeignProperties feignProperties;

    public FeignLogger(FeignProperties feignProperties) {
        this(FeignLogger.class, feignProperties);
    }

    public FeignLogger(Class<?> clazz, FeignProperties feignProperties) {
        this.log = JsonLogger.getInstance(clazz);
        this.feignProperties = feignProperties;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        FeignLogInfo logInfo = new FeignLogInfo();
        logInfo.setClassMethod(StringUtils.trim(methodTag(configKey)));
        logInfo.setUrl(request.url());
        logInfo.setHttpMethod(request.httpMethod().name());
        if (!FeignLogUtil.isIgnoreLogHeader(getMethodName(configKey), feignProperties)) {
            logInfo.setHeaderParams(request.headers());
        }
        if (!FeignLogUtil.isIgnoreLogRequest(getMethodName(configKey), feignProperties)) {
            if (request.body() != null) {
                String bodyText = request.charset() != null ? new String(request.body(), request.charset()) : null;
                bodyText = bodyText != null ? bodyText : "Binary data";
                logInfo.setRequestBody(bodyText);
            }
            RequestTemplate template = request.requestTemplate();
            if (Objects.nonNull(template)) {
                Map<String, Collection<String>> queryParams = template.queries();
                logInfo.setQueryParams(queryParams);
            }
        }
        log.info("request log info => {}", LyJsonUtil.toJson4Log(logInfo));
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        FeignLogInfo logInfo = new FeignLogInfo();
        int status = response.status();
        if (response.body() != null && !(status == 204 || status == 205)) {
            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            if (!FeignLogUtil.isIgnoreLogResponse(getMethodName(configKey), feignProperties)) {
                logInfo.setResponseBody(decodeOrDefault(bodyData, UTF_8, "Binary data"));
            }
            response = response.toBuilder().body(bodyData).build();
            logInfo.setElapsedTime(elapsedTime);
            try {
                LyMDCUtil.put(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME, String.valueOf(elapsedTime));
                log.info("response log info => {}", LyJsonUtil.toJson4Log(logInfo));
            } finally {
                LyMDCUtil.remove(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME);
            }
        }
        return response;
    }

    @Override
    protected IOException logIOException(String configKey, Level logLevel, IOException ioe, long elapsedTime) {
        FeignLogInfo logInfo = new FeignLogInfo();
        try {
            logInfo.setExceptionStackTrace(LyThrowableUtil.getStackTrace(ioe));
            logInfo.setElapsedTime(elapsedTime);
            LyMDCUtil.put(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME, String.valueOf(elapsedTime));
            log.error("response log info => {}", LyJsonUtil.toJson4Log(logInfo));
        } finally {
            LyMDCUtil.remove(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME);
        }
        return ioe;
    }

    private String getMethodName(String configKey) {
        String classMethod = configKey.substring(0, configKey.indexOf(Symbols.PARENTHESES_LEFT));
        String[] classMethodArray = StringUtils.split(classMethod, '#');
        if (Objects.nonNull(classMethodArray) && classMethodArray.length == 2) {
            return classMethodArray[1];
        }
        return "feign-default";
    }
}
