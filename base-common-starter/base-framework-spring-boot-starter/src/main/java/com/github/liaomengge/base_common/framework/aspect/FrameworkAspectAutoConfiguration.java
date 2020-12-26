package com.github.liaomengge.base_common.framework.aspect;

import com.github.liaomengge.base_common.framework.FrameworkProperties;
import com.github.liaomengge.base_common.framework.consts.FrameworkConst;
import com.github.liaomengge.base_common.utils.aop.LyExpressionUtil;
import com.github.liaomengge.service.base_framework.api.BaseFrameworkServiceApi;
import com.github.liaomengge.service.base_framework.common.config.FilterConfig;
import com.github.liaomengge.service.base_framework.common.filter.aspect.ServiceApiAspect;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import com.github.liaomengge.service.base_framework.common.filter.chain.ServiceApiFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2020/11/9.
 * <p>
 * SentinelBeanPostProcessor非static定义，导致所有AOP切面定义的对象都会被提前加载，导致BeanPostProcessorChecker检查失败
 *
 * @see com.alibaba.cloud.sentinel.custom.SentinelAutoConfiguration#sentinelBeanPostProcessor(ApplicationContext)
 */
@Configuration(proxyBeanMethods = false)
public class FrameworkAspectAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final FrameworkProperties frameworkProperties;

    public FrameworkAspectAutoConfiguration(FrameworkProperties frameworkProperties) {
        this.frameworkProperties = frameworkProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean("com.github.liaomengge.service.base_framework.common.config.FilterConfig")
    @ConditionalOnMissingBean
    @ConfigurationProperties(FrameworkConst.CONFIGURATION_PROPERTIES_PREFIX)
    public FilterConfig filterConfig() {
        return new FilterConfig();
    }

    @Bean("com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain")
    @ConditionalOnMissingBean
    public FilterChain filterChain() {
        FilterChain filterChain = new FilterChain();
        Map<String, ServiceApiFilter> serviceFilterMap = applicationContext.getBeansOfType(ServiceApiFilter.class);
        Optional.ofNullable(serviceFilterMap).ifPresent(val -> filterChain.addFilter(val.values().parallelStream().collect(Collectors.toList())));
        return filterChain;
    }

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
        FrameworkProperties.ControllerAspectProperties controllerAspectProperties =
                frameworkProperties.getControllerAspect();
        String[] basePackages = controllerAspectProperties.getBasePackages();
        if (ArrayUtils.isNotEmpty(basePackages)) {
            String executions = LyExpressionUtil.getPackagesExpression(basePackages);
            return "(" + executions + ")" + LyExpressionUtil.AND +
                    "(" + controllerExpression + ")" + LyExpressionUtil.AND +
                    "!@annotation(org.springframework.web.bind.annotation.InitBinder)";
        }
        return null;
    }
}
