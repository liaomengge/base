package com.github.liaomengge.base_common.helper.retrofit.client.interceptor;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.github.liaomengge.base_common.helper.retrofit.consts.RetrofitMetricsConst;
import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
import com.github.liaomengge.base_common.utils.error.LyExceptionUtil;
import com.github.liaomengge.base_common.utils.url.LyMoreUrlUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by liaomengge on 2019/11/5.
 */
@Slf4j
@AllArgsConstructor
public class SentinelRetrofitInterceptor implements Interceptor {

    private MeterRegistry meterRegistry;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response;

        Entry hostWithPathEntry = null;
        String hostWithPathResource = null;
        try {
            String url = this.buildReqUrl(request);
            hostWithPathResource = request.method() + ":" + url;
            hostWithPathEntry = SphU.entry(hostWithPathResource, EntryType.OUT);
            response = chain.proceed(request);
        } catch (BlockException e) {
            if (StringUtils.isNotBlank(hostWithPathResource)) {
                log.warn("Resource[{}], Retrofit Block Exception...", hostWithPathResource);
                String methodSuffix = LyMoreUrlUtil.getUrlSuffix(hostWithPathResource);
                _MeterRegistrys.counter(meterRegistry, methodSuffix + RetrofitMetricsConst.REQ_EXE_BLOCKED).ifPresent(Counter::increment);
            }
            throw LyExceptionUtil.unchecked(e);
        } catch (Throwable t) {
            Tracer.trace(t);
            throw t;
        } finally {
            if (hostWithPathEntry != null) {
                hostWithPathEntry.exit();
            }
        }
        return response;
    }

    private String buildReqUrl(Request request) {
        return request.url().toString();
    }
}
