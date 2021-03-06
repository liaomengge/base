//package com.github.liaomengge.base_common.dayu.sentinel.interceptor;
//
//import com.github.liaomengge.base_common.dayu.domain.DayuBlockedDomain;
//import com.github.liaomengge.base_common.dayu.sentinel.SentinelProperties;
//import com.github.liaomengge.base_common.utils.web.LyWebUtil;
//import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//
///**
// * Created by liaomengge on 2019/8/12.
// */
//@Configuration
//public class SentinelWebMvcConfigurer extends WebMvcConfigurerAdapter {
//
//    @Autowired
//    private SentinelProperties sentinelProperties;
//
//    @Autowired
//    private SentinelHandlerInterceptor sentinelHandlerInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        String[] patterns = {"/**"};
//        List<String> urlPatterns = sentinelProperties.getInterceptor().getUrlPatterns();
//        if (CollectionUtils.isNotEmpty(urlPatterns)) {
//            patterns = urlPatterns.stream().toArray(String[]::new);
//        }
//        registry.addInterceptor(sentinelHandlerInterceptor)
//                .addPathPatterns(patterns).order(sentinelProperties.getInterceptor().getOrder());
//    }
//
//    @PostConstruct
//    private void init() {
//        WebCallbackManager.setUrlBlockHandler((request, response, ex) -> LyWebUtil.renderJson(response,
//                DayuBlockedDomain.create()));
//    }
//}
