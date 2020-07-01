package cn.ly.base_common.dayu.sentinel.filter;

import cn.ly.base_common.dayu.consts.DayuConst;
import cn.ly.base_common.utils.log4j2.MwLogger;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.adapter.servlet.config.WebServletConfig;
import com.alibaba.csp.sentinel.adapter.servlet.util.FilterUtil;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.timgroup.statsd.StatsDClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static cn.ly.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2019/11/7.
 */
public class SentinelFilter implements Filter, EnvironmentAware {

    private static final Logger logger = MwLogger.getInstance(SentinelFilter.class);

    private static final String FRAMEWORK_SENTINEL_ENABLED = "ly.framework.sentinel.enabled";

    private List<String> excludedUris;

    private StatsDClient statsDClient;

    private Environment environment;

    public SentinelFilter(StatsDClient statsDClient) {
        this.statsDClient = statsDClient;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludePattern = filterConfig.getInitParameter("excludedUris");
        if (StringUtils.isNotBlank(excludePattern)) {
            excludedUris = SPLITTER.splitToList(excludePattern);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!this.isFrameworkSentinelEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String origin = "", uriTarget = "";
        Entry urlEntry = null;
        try {
            uriTarget = FilterUtil.filterTarget(httpServletRequest);
            UrlCleaner urlCleaner = WebCallbackManager.getUrlCleaner();
            if (urlCleaner != null) {
                uriTarget = urlCleaner.clean(uriTarget);
            }
            if (StringUtils.isNotBlank(uriTarget) && !isExcludeUri(uriTarget)) {
                origin = parseOrigin(httpServletRequest);
                logger.info("[Sentinel Filter] Origin: {}, enter Uri Path: {}", origin, uriTarget);
                ContextUtil.enter(WebServletConfig.WEB_SERVLET_CONTEXT_NAME, origin);
                urlEntry = SphU.entry(uriTarget, EntryType.IN);
            }
            filterChain.doFilter(request, response);
        } catch (BlockException e) {
            logger.warn("[Sentinel Filter] Block Exception when Origin: " + origin + " enter fall back uri: " + uriTarget, e);
            WebCallbackManager.getUrlBlockHandler().blocked(httpServletRequest, httpServletResponse, e);
            String finalUriTarget = uriTarget;
            Optional.ofNullable(statsDClient).ifPresent(val -> statsDClient.increment(DayuConst.METRIC_SENTINEL_BLOCKED_PREFIX + finalUriTarget));
        } catch (IOException | ServletException | RuntimeException e2) {
            Tracer.traceEntry(e2, urlEntry);
            throw e2;
        } finally {
            if (urlEntry != null) {
                urlEntry.exit();
            }
            ContextUtil.exit();
        }
    }

    private boolean isFrameworkSentinelEnabled() {
        Boolean frameworkEnabled = this.environment.getProperty(FRAMEWORK_SENTINEL_ENABLED, Boolean.class);
        return BooleanUtils.toBooleanDefaultIfNull(frameworkEnabled, false);
    }

    private boolean isExcludeUri(String uri) {
        return CollectionUtils.isNotEmpty(excludedUris) && excludedUris.contains(uri);
    }

    private String parseOrigin(HttpServletRequest request) {
        RequestOriginParser originParser = WebCallbackManager.getRequestOriginParser();
        String origin = StringUtils.EMPTY;
        if (originParser != null) {
            origin = originParser.parseOrigin(request);
            if (StringUtils.isEmpty(origin)) {
                return StringUtils.EMPTY;
            }
        }
        return origin;
    }

    @Override
    public void destroy() {
    }
}
