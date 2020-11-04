package com.github.liaomengge.base_common.helper.retrofit.client.interceptor;

import com.github.liaomengge.base_common.helper.retrofit.consts.RetrofitMetricsConst;
import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
import com.github.liaomengge.base_common.support.misc.Charsets;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.log.LyAlarmLogUtil;
import com.github.liaomengge.base_common.utils.log.LyMDCUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.url.LyMoreUrlUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Setter;
import okhttp3.*;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/3/1.
 */
public class HttpLoggingInterceptor implements Interceptor {

    private static final Logger log = LyLogger.getInstance(HttpLoggingInterceptor.class);

    private final String projName;

    @Setter
    private String ignoreLogMethodName;//逗号分隔

    @Setter
    private MeterRegistry meterRegistry;

    public HttpLoggingInterceptor(String projName) {
        this.projName = projName;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;

        long startNs = System.nanoTime(), tookMs;
        try {
            response = chain.proceed(request);
        } catch (Throwable t) {
            if (t instanceof InterruptedIOException || (Objects.nonNull(t.getCause()) && t.getCause() instanceof InterruptedIOException)) {
                LyAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_HTTP.error(t);
            } else {
                LyAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_BIZ.error(t);
            }
            tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            LyMDCUtil.put(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME, String.valueOf(tookMs));
            if (isIgnoreLogMethod(request)) {
                log.error("url ==> [{}], exception reason ===> [{}], elapsed time[{}]ms",
                        buildReqUrl(request), LyThrowableUtil.getStackTrace(t), tookMs);
            } else {
                log.error("url ==> [{}], request params ==> [{}], exception reason ===> [{}], elapsed time[{}]ms",
                        buildReqUrl(request), buildReqStr(request), LyThrowableUtil.getStackTrace(t), tookMs);
            }
            throw t;
        } finally {
            try {
                tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                LyMDCUtil.put(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME, String.valueOf(tookMs));
                doFinally(request, response, System.nanoTime() - startNs);
            } finally {
                LyMDCUtil.remove(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME);
            }
        }
        return response;
    }

    private void doFinally(Request request, Response response, long execTime) throws IOException {
        String reqUrl = buildReqUrl(request);
        String prefix = buildMetricsPrefixName(reqUrl);

        String reqStr = buildReqStr(request);
        String respStr = "";
        if (Objects.nonNull(response)) {
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody) && HttpHeaders.hasBody(response) && response.isSuccessful()) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.getBuffer();

                Charset charset;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(Charsets.UTF_8);
                    respStr = buffer.clone().readString(charset);
                }

                if (isIgnoreLogMethod(request)) {
                    log.info("url ==> [{}], response result ===> [{}], elapsed time[{}]ms", reqUrl, respStr, execTime);
                } else {
                    log.info("url ==> [{}], request params ==> [{}], response result ===> [{}], elapsed time[{}]ms",
                            reqUrl, reqStr, respStr, execTime);
                }
                statIncrement(prefix + RetrofitMetricsConst.REQ_EXE_SUC);
            } else {
                if (isIgnoreLogMethod(request)) {
                    log.warn("url ==> [{}], error code[{}], response result ===> [{}], elapsed time[{}]ms", reqUrl,
                            response.code(), response.message(), execTime);
                } else {
                    log.warn("url ==> [{}], request params ==> [{}], error code[{}], response result ===> [{}], " +
                                    "elapsed time[{}]ms",
                            reqUrl, reqStr, response.code(), response.message(), execTime);
                }
                statIncrement(prefix + RetrofitMetricsConst.REQ_EXE_FAIL);
            }
        }

        statRecord(prefix + RetrofitMetricsConst.REQ_EXE_TIME, execTime);
    }

    private String buildMetricsPrefixName(String url) {
        String urlMethod = LyMoreUrlUtil.getUrlSuffix(url);
        return projName + "." + urlMethod;
    }

    private String buildReqUrl(Request request) {
        return request.url().toString();
    }

    private String buildReqStr(Request request) throws IOException {
        String reqStr = "";
        RequestBody requestBody = request.body();
        if (Objects.nonNull(requestBody)) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(Charsets.UTF_8);
                reqStr = buffer.readString(charset);
            }
        }
        return reqStr;
    }

    private boolean isIgnoreLogMethod(Request request) {
        if (StringUtils.isNotBlank(ignoreLogMethodName)) {
            String methodName = LyMoreUrlUtil.getUrlSuffix(buildReqUrl(request));
            Iterable<String> iterable =
                    Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings().split(ignoreLogMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    private void statIncrement(String metric) {
        _MeterRegistrys.counter(meterRegistry, metric).ifPresent(Counter::increment);
    }

    private void statRecord(String metric, long execTime) {
        _MeterRegistrys.timer(meterRegistry, metric).ifPresent(val -> val.record(Duration.ofNanos(execTime)));
    }
}
