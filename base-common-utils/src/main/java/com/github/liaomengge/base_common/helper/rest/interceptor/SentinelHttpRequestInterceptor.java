package com.github.liaomengge.base_common.helper.rest.interceptor;

import com.github.liaomengge.base_common.helper.rest.consts.ReqMetricsConst;
import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
import com.github.liaomengge.base_common.utils.error.LyExceptionUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.url.LyMoreUrlUtil;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;

/**
 * Created by liaomengge on 2019/11/5.
 */
@AllArgsConstructor
public class SentinelHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LyLogger.getInstance(SentinelHttpRequestInterceptor.class);

    private MeterRegistry meterRegistry;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response;

        Entry hostWithPathEntry = null;
        String hostWithPathResource = null;
        try {
            URI uri = request.getURI();
            String hostResource =
                    request.getMethod().toString() + ":" + uri.getScheme() + "://" + uri.getHost() + (uri.getPort() == -1 ? "" : ":" + uri.getPort());
            hostWithPathResource = hostResource + uri.getPath();
            hostWithPathEntry = SphU.entry(hostWithPathResource, EntryType.OUT);
            response = execution.execute(request, body);
        } catch (BlockException e) {
            if (StringUtils.isNotBlank(hostWithPathResource)) {
                log.warn("Resource[{}], RestTemplate Block Exception...", hostWithPathResource);
                String methodSuffix = LyMoreUrlUtil.getUrlSuffix(hostWithPathResource);
                _MeterRegistrys.counter(meterRegistry, methodSuffix + ReqMetricsConst.REQ_EXE_BLOCKED).ifPresent(Counter::increment);
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
}
