package cn.ly.base_common.helper.rest.sync.interceptor;

import cn.ly.base_common.utils.trace.LyTraceLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by liaomengge on 2019/11/21.
 */
public class HttpHeaderInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
        String traceId = LyTraceLogUtil.get();
        if (StringUtils.isNotBlank(traceId)) {
            httpRequest.addHeader(LyTraceLogUtil.TRACE_ID, traceId);
        }
    }
}