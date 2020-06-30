//package cn.ly.base_common.dayu.sentinel.interceptor;
//
//import cn.ly.base_common.dayu.domain.DayuBlockedDomain;
//import cn.ly.base_common.dayu.sentinel.SentinelProperties;
//import cn.ly.base_common.utils.web.MwWebUtil;
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
//        WebCallbackManager.setUrlBlockHandler((request, response, ex) -> MwWebUtil.renderJson(response,
//                DayuBlockedDomain.create()));
//    }
//}
