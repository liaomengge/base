package com.github.liaomengge.base_common.framework.aspect;

import com.github.liaomengge.base_common.framework.FrameworkProperties;
import com.github.liaomengge.base_common.utils.aop.LyExpressionUtil;
import com.github.liaomengge.service.base_framework.api.BaseFrameworkServiceApi;
import com.github.liaomengge.service.base_framework.common.config.FilterConfig;
import com.github.liaomengge.service.base_framework.common.filter.aspect.ServiceApiAspect;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liaomengge on 2020/11/9.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
public class FrameworkAspectConfiguration {

    private final FrameworkProperties frameworkProperties;

    @Bean("com.github.liaomengge.service.base_framework.common.filter.aspect.ServiceApiAspect")
    @ConditionalOnMissingBean
    public ServiceApiAspect serviceApiAspect(FilterConfig filterConfig, FilterChain filterChain) {
        ServiceApiAspect serviceAspect = new ServiceApiAspect();
        serviceAspect.setFilterConfig(filterConfig);
        serviceAspect.setFilterChain(filterChain);
        return serviceAspect;
    }

    @Bean("serviceApiDefaultPointcutAdvisor")
    @ConditionalOnMissingBean(name = "serviceApiDefaultPointcutAdvisor")
    public DefaultPointcutAdvisor defaultPointcutAdvisor(ServiceApiAspect serviceApiAspect) {
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        AspectJExpressionPointcut expressionPointcut = new AspectJExpressionPointcut();
        String frameworkExpression = LyExpressionUtil.getTargetExpression(BaseFrameworkServiceApi.class);
        String apiExpression = getApiExpression();
        if (StringUtils.isNoneBlank(apiExpression)) {
            frameworkExpression = "(" + frameworkExpression + ")" + LyExpressionUtil.OR + "(" + apiExpression + ")";
        }
        frameworkExpression =
                LyExpressionUtil.getNonStaticExpression() + LyExpressionUtil.AND + "(" + frameworkExpression + ")";
        expressionPointcut.setExpression(frameworkExpression);
        defaultPointcutAdvisor.setPointcut(expressionPointcut);
        defaultPointcutAdvisor.setAdvice(serviceApiAspect);
        return defaultPointcutAdvisor;
    }

    private String getApiExpression() {
        String controllerExpression = LyExpressionUtil.getWithinAnnotationExpression(Controller.class,
                RestController.class);
        FrameworkProperties.ControllerAopProperties controllerAopProperties = frameworkProperties.getControllerAop();
        String[] basePackages = controllerAopProperties.getBasePackages();
        if (ArrayUtils.isNotEmpty(basePackages)) {
            String executions = LyExpressionUtil.getPackagesExpression(basePackages);
            return "(" + executions + ")" + LyExpressionUtil.AND +
                    "(" + controllerExpression + ")" + LyExpressionUtil.AND +
                    "!@annotation(org.springframework.web.bind.annotation.InitBinder)";
        }
        return null;
    }
}
