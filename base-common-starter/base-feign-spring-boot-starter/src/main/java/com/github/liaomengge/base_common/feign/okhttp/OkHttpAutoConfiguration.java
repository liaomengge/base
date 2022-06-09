package com.github.liaomengge.base_common.feign.okhttp;

import com.github.liaomengge.base_common.feign.FeignProperties;
import feign.Feign;
import feign.okhttp.OkHttpClient;
import lombok.AllArgsConstructor;
import okhttp3.ConnectionPool;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/10/21.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Feign.class, OkHttpClient.class})
@AutoConfigureBefore({FeignRibbonClientAutoConfiguration.class, FeignAutoConfiguration.class})
@ConditionalOnProperty(value = {"feign.okhttp.enabled", "base.feign.okhttp.enabled"}, havingValue = "true")
public class OkHttpAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
    @EnableConfigurationProperties(FeignProperties.class)
    protected static class OkHttpFeignConfiguration {

        private okhttp3.OkHttpClient okHttpClient;

        private final FeignProperties feignProperties;

        public OkHttpFeignConfiguration(FeignProperties feignProperties) {
            this.feignProperties = feignProperties;
        }

        @Bean
        @ConditionalOnMissingBean(ConnectionPool.class)
        public ConnectionPool httpClientConnectionPool(OkHttpClientConnectionPoolFactory connectionPoolFactory) {
            FeignProperties.OkHttpProperties okHttpProperties = feignProperties.getOkhttp();
            Integer maxTotalConnections = okHttpProperties.getMaxConnections();
            Long timeToLive = okHttpProperties.getTimeToLive();
            TimeUnit ttlUnit = okHttpProperties.getTimeToLiveUnit();
            return connectionPoolFactory.create(maxTotalConnections, timeToLive, ttlUnit);
        }

        @Bean
        public okhttp3.OkHttpClient client(OkHttpClientFactory httpClientFactory, ConnectionPool connectionPool) {
            FeignProperties.OkHttpProperties okHttpProperties = feignProperties.getOkhttp();
            Boolean followRedirects = okHttpProperties.isFollowRedirects();
            Long connectTimeout = okHttpProperties.getConnectTimeout();
            Long readTimeout = okHttpProperties.getReadTimeout();
            Long writeTimeout = okHttpProperties.getWriteTimeout();
            Boolean disableSslValidation = okHttpProperties.isDisableSslValidation();
            this.okHttpClient = httpClientFactory.createBuilder(disableSslValidation)
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .followRedirects(followRedirects).connectionPool(connectionPool)
                    .build();
            return this.okHttpClient;
        }

        @PreDestroy
        public void destroy() {
            if (this.okHttpClient != null) {
                this.okHttpClient.dispatcher().executorService().shutdown();
                this.okHttpClient.connectionPool().evictAll();
            }
        }
    }
}
