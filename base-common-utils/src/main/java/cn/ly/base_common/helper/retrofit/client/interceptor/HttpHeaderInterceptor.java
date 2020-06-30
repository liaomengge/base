package cn.ly.base_common.helper.retrofit.client.interceptor;

import cn.ly.base_common.utils.trace.MwTraceLogUtil;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by liaomengge on 2019/11/21.
 */
public class HttpHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String traceId = MwTraceLogUtil.get();
        if (StringUtils.isNotBlank(traceId)) {
            Request.Builder requestBuilder = original.newBuilder().addHeader(MwTraceLogUtil.TRACE_ID, traceId);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
        return chain.proceed(original);
    }
}
