package com.github.liaomengge.base_common.dayu.guava.interceptor;

import com.github.liaomengge.base_common.dayu.guava.GuavaRateLimitProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created by liaomengge on 2019/8/12.
 */
@Configuration
public class GuavaRateLimitWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private GuavaRateLimitProperties guavaRateLimitProperties;

    @Autowired
    private GuavaRateLimitHandlerInterceptor guavaRateLimitHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] patterns = {"/**"};
        List<String> urlPatterns = guavaRateLimitProperties.getInterceptor().getUrlPatterns();
        if (CollectionUtils.isNotEmpty(urlPatterns)) {
            patterns = urlPatterns.stream().toArray(String[]::new);
        }
        registry.addInterceptor(guavaRateLimitHandlerInterceptor)
                .addPathPatterns(patterns).order(guavaRateLimitProperties.getInterceptor().getOrder());
    }
}
