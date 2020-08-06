package cn.ly.base_common.framework.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Created by liaomengge on 2020/8/4.
 */
@Configuration
public class FrameworkCorsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //是否发送Cookie信息
        corsConfiguration.setAllowCredentials(true);
        //放行哪些原始域
        corsConfiguration.addAllowedOrigin("*");
        //放行哪些原始域(请求方式)
        corsConfiguration.addAllowedMethod("*");
        //放行哪些原始域(头部信息)
        corsConfiguration.addAllowedHeader("*");
        //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        corsConfiguration.addExposedHeader("*");

        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(configSource);
    }

}
