package com.github.liaomengge.base_common.swagger.security;

import com.github.xiaoymin.knife4j.spring.filter.ProductionSecurityFilter;
import com.github.xiaoymin.knife4j.spring.filter.SecurityBasicAuthFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by liaomengge on 2019/7/12.
 */
@Configuration(proxyBeanMethods = false)
public class ExtendSecurityConfiguration {

    @Autowired(required = false)
    private Environment environment;

    @Bean
    @ConditionalOnMissingBean
    public ProductionSecurityFilter productionSecurityFilter() {
        boolean prod = false;
        if (environment != null) {
            Boolean enabled = environment.getProperty("base.swagger.enabled", Boolean.class);
            prod = BooleanUtils.toBooleanDefaultIfNull(enabled, false);
        }
        ProductionSecurityFilter p = new ProductionSecurityFilter(!prod);
        return p;
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityBasicAuthFilter securityBasicAuthFilter() {
        boolean enableSwaggerBasicAuth = false;
        String defaultUserName = "admin", defaultPass = "123456";
        if (environment != null) {
            Boolean enableAuth = environment.getProperty("base.swagger.basic.enabled", Boolean.class);
            enableSwaggerBasicAuth = BooleanUtils.toBooleanDefaultIfNull(enableAuth, false);
            if (enableSwaggerBasicAuth) {
                //如果开启basic验证,从配置文件中获取用户名和密码
                String pUser = environment.getProperty("base.swagger.basic.username");
                String pPass = environment.getProperty("base.swagger.basic.password");
                if (pUser != null && !"".equals(pUser)) {
                    defaultUserName = pUser;
                }
                if (pPass != null && !"".equals(pPass)) {
                    defaultPass = pPass;
                }
            }
        }
        SecurityBasicAuthFilter securityBasicAuthFilter = new SecurityBasicAuthFilter(enableSwaggerBasicAuth,
                defaultUserName, defaultPass);
        return securityBasicAuthFilter;
    }
}
