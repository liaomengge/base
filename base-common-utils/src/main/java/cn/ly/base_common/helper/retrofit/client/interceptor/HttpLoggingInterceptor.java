package cn.ly.base_common.helper.retrofit.client.interceptor;

import cn.ly.base_common.utils.error.MwThrowableUtil;
import cn.ly.base_common.utils.log.MwAlarmLogUtil;
import cn.ly.base_common.utils.log.MwMDCUtil;
import cn.ly.base_common.utils.log4j2.MwLogger;
import cn.ly.base_common.utils.url.MwMoreUrlUtil;
import cn.ly.base_common.helper.retrofit.consts.RetrofitMetricsConst;
import cn.ly.base_common.support.misc.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.timgroup.statsd.StatsDClient;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/3/1.
 */
public class HttpLoggingInterceptor implements Interceptor {

    private static final Logger logger = MwLogger.getInstance(HttpLoggingInterceptor.class);

    private final String projName;

    @Setter
    private String ignoreLogMethodName;//逗号分隔

    @Setter
    private StatsDClient statsDClient;

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
                MwAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_HTTP.error(t);
            } else {
                MwAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_BIZ.error(t);
            }
            tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            MwMDCUtil.put(MwMDCUtil.MDC_THIRD_ELAPSED_TIME, String.valueOf(tookMs));
            if (isIgnoreLogMethod(request)) {
                logger.warn("请求路径 ==> [{}], 请求异常 ===> [{}], 耗时[{}]ms",
                        buildReqUrl(request), MwThrowableUtil.getStackTrace(t), tookMs);
            } else {
                logger.warn("请求路径 ==> [{}], 请求参数 ==> [{}], 请求异常 ===> [{}], 耗时[{}]ms",
                        buildReqUrl(request), buildReqStr(request), MwThrowableUtil.getStackTrace(t), tookMs);
            }
            throw t;
        } finally {
            try {
                tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                MwMDCUtil.put(MwMDCUtil.MDC_THIRD_ELAPSED_TIME, String.valueOf(tookMs));
                doFinally(request, response, tookMs);
            } finally {
                MwMDCUtil.remove(MwMDCUtil.MDC_THIRD_ELAPSED_TIME);
            }
        }
        return response;
    }

    private void doFinally(Request request, Response response, long tookMs) throws IOException {
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
                    logger.info("请求路径 ==> [{}], 返回信息 ===> [{}], 耗时[{}]ms", reqUrl, respStr, tookMs);
                } else {
                    logger.info("请求路径 ==> [{}], 请求参数 ==> [{}], 返回信息 ===> [{}], 耗时[{}]ms", reqUrl, reqStr,
                            respStr, tookMs);
                }
                statIncrement(prefix + RetrofitMetricsConst.REQ_EXE_SUC);
            } else {
                if (isIgnoreLogMethod(request)) {
                    logger.warn("请求路径 ==> [{}], 错误码[{}], 返回信息 ===> [{}], 耗时[{}]ms", reqUrl,
                            response.code(), response.message(), tookMs);
                } else {
                    logger.warn("请求路径 ==> [{}], 请求参数 ==> [{}], 错误码[{}], 返回信息 ===> [{}], 耗时[{}]ms", reqUrl,
                            reqStr, response.code(), response.message(), tookMs);
                }
                statIncrement(prefix + RetrofitMetricsConst.REQ_EXE_FAIL);
            }
        }

        statRecord(prefix + RetrofitMetricsConst.REQ_EXE_TIME, tookMs);
    }

    private String buildMetricsPrefixName(String url) {
        String urlMethod = MwMoreUrlUtil.getUrlSuffix(url);
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
            String methodName = MwMoreUrlUtil.getUrlSuffix(buildReqUrl(request));
            Iterable<String> iterable =
                    Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings().split(ignoreLogMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    private void statIncrement(String metric) {
        if (Objects.nonNull(statsDClient)) {
            statsDClient.increment(metric);
        }
    }

    private void statRecord(String metric, long execTime) {
        if (Objects.nonNull(statsDClient)) {
            statsDClient.recordExecutionTime(metric, execTime);
        }
    }
}
