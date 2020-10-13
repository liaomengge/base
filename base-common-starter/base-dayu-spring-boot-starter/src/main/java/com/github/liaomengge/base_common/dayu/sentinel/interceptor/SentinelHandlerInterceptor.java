//package com.github.liaomengge.base_common.dayu.sentinel.interceptor;
//
//import com.github.liaomengge.base_common.dayu.consts.DayuConst;
//import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
//import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
//import com.alibaba.csp.sentinel.EntryType;
//import com.alibaba.csp.sentinel.SphU;
//import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
//import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
//import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
//import com.alibaba.csp.sentinel.adapter.servlet.util.FilterUtil;
//import com.alibaba.csp.sentinel.context.ContextUtil;
//import com.alibaba.csp.sentinel.slots.block.BlockException;
//import com.alibaba.csp.sentinel.util.StringUtil;
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import lombok.AllArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.springframework.web.servlet.HandlerMapping;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * Created by liaomengge on 2019/8/12.
// */
//@AllArgsConstructor
//public class SentinelHandlerInterceptor extends HandlerInterceptorAdapter {
//
//    private static final Logger log = LyLogger.getInstance(SentinelHandlerInterceptor.class);
//
//    private static final String WEB_INTERCEPTOR_CONTEXT_NAME = "sentinel_web_interceptor_context";
//
//    private MeterRegistry meterRegistry;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
//            Exception {
//        String origin = "", uriTarget = "";
//        try {
//            origin = parseOrigin(request);
//            String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
//            uriTarget = StringUtils.defaultString(pattern, FilterUtil.filterTarget(request));
//
//            UrlCleaner urlCleaner = WebCallbackManager.getUrlCleaner();
//            if (urlCleaner != null) {
//                uriTarget = urlCleaner.clean(uriTarget);
//            }
//            log.info("[Sentinel Pre Filter] Origin: {} enter Uri Path: {}", origin, uriTarget);
//            ContextUtil.enter(WEB_INTERCEPTOR_CONTEXT_NAME, origin);
//            SphU.entry(uriTarget, EntryType.IN);
//            return true;
//        } catch (BlockException ex) {
//            log.warn(String.format("[Sentinel Pre Filter] Block Exception when Origin: %s enter fall back uri: %s",
//                    origin, uriTarget), ex);
//            WebCallbackManager.getUrlBlockHandler().blocked(request, response, ex);
//            String finalUriTarget = uriTarget;
//            _MeterRegistrys.counter(meterRegistry, DayuConst.METRIC_SENTINEL_BLOCKED_PREFIX + finalUriTarget)
//            .ifPresent(Counter::increment);
//            return false;
//        }
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                           ModelAndView modelAndView) throws Exception {
//        while (ContextUtil.getContext() != null && ContextUtil.getContext().getCurEntry() != null) {
//            ContextUtil.getContext().getCurEntry().exit();
//        }
//        ContextUtil.exit();
//    }
//
//    private String parseOrigin(HttpServletRequest request) {
//        RequestOriginParser originParser = WebCallbackManager.getRequestOriginParser();
//        String origin = StringUtils.EMPTY;
//        if (originParser != null) {
//            origin = originParser.parseOrigin(request);
//            if (StringUtil.isEmpty(origin)) {
//                return StringUtils.EMPTY;
//            }
//        }
//        return origin;
//    }
//}
