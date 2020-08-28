package cn.ly.base_common.helper.rest.interceptor;

import cn.ly.base_common.helper.rest.consts.ReqMetricsConst;
import cn.ly.base_common.utils.error.LyExceptionUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.url.LyMoreUrlUtil;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * Created by liaomengge on 2019/11/5.
 */
@AllArgsConstructor
public class SentinelHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LyLogger.getInstance(SentinelHttpRequestInterceptor.class);

    private StatsDClient statsDClient;

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
                Optional.ofNullable(statsDClient).ifPresent(val -> statsDClient.increment(methodSuffix + ReqMetricsConst.REQ_EXE_BLOCKED));
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
