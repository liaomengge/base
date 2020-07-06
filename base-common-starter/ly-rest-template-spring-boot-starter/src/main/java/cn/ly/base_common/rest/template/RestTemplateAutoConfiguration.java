package cn.ly.base_common.rest.template;

import cn.ly.base_common.helper.rest.interceptor.SentinelHttpRequestInterceptor;
import cn.ly.base_common.helper.rest.sync.SyncClientTemplate;
import cn.ly.base_common.helper.rest.sync.interceptor.HttpHeaderInterceptor;
import cn.ly.base_common.helper.rest.sync.retry.HttpRetryHandler;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.thread.LyThreadFactoryBuilderUtil;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2018/11/1.
 */
@Configuration
@ConditionalOnClass(SyncClientTemplate.class)
@EnableConfigurationProperties(RestTemplateProperties.class)
public class RestTemplateAutoConfiguration {

    private static final Logger logger = LyLogger.getInstance(RestTemplateAutoConfiguration.class);

    private static final String DAYU_SENTINEL_ENABLED = "ly.dayu.sentinel.enabled";
    private static final String SPRING_APPLICATION_NAME = "spring.application.name";

    @Autowired(required = false)
    private Environment environment;

    @Autowired
    private RestTemplateProperties restTemplateProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(StatsDClient.class)
    public SentinelHttpRequestInterceptor sentinelHttpRequestInterceptor(StatsDClient statsDClient) {
        return new SentinelHttpRequestInterceptor(statsDClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        RestTemplateProperties.HttpClientProperties httpClientProperties = this.restTemplateProperties.getHttp();

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
                new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(httpClientProperties.getMaxTotal());
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(httpClientProperties.getDefaultMaxPerRoute());
        poolingHttpClientConnectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(httpClientProperties.getReadTimeout()).build());

        List<RestTemplateProperties.UrlHttpClientProperties> urlHttpClientProperties = httpClientProperties.getUrls();
        if (CollectionUtils.isNotEmpty(urlHttpClientProperties)) {
            urlHttpClientProperties.forEach(val -> {
                try {
                    String url = val.getUrl();
                    HttpHost httpHost = URIUtils.extractHost(URI.create(url));
                    HttpRoute httpRoute = new HttpRoute(httpHost);
                    Integer maxPerRoute = val.getMaxPerRoute();
                    Integer readTimeout = val.getReadTimeout();
                    Optional.ofNullable(maxPerRoute).ifPresent(val2 -> poolingHttpClientConnectionManager.setMaxPerRoute(httpRoute, maxPerRoute));
                    Optional.ofNullable(readTimeout).ifPresent(val2 -> poolingHttpClientConnectionManager.setSocketConfig(httpHost, SocketConfig.custom().setSoTimeout(readTimeout).build()));
                } catch (Exception e) {
                    logger.warn("rest template http client properties url illegal", e);
                }
            });
        }

        closeIdleExpiredConnections(poolingHttpClientConnectionManager);

        return poolingHttpClientConnectionManager;
    }

    @RefreshScope
    @Bean
    @ConditionalOnBean(PoolingHttpClientConnectionManager.class)
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
                                     SentinelHttpRequestInterceptor sentinelHttpRequestInterceptor) {
        RestTemplateProperties.HttpClientProperties httpClientProperties = this.restTemplateProperties.getHttp();
        RestTemplate restTemplate = new RestTemplate();

        HttpRetryHandler retryHandler = new HttpRetryHandler(httpClientProperties.getRetryCount());

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
        httpClientBuilder.setRetryHandler(retryHandler);
        if (httpClientProperties.isEnabledTraceHeader()) {
            httpClientBuilder.addInterceptorFirst(new HttpHeaderInterceptor());
        }

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClientBuilder.build());
        requestFactory.setConnectTimeout(httpClientProperties.getConnectionTimeout());
        requestFactory.setConnectionRequestTimeout(httpClientProperties.getConnectionRequestTimeout());

        List<HttpMessageConverter<?>> messageConverters = Lists.newArrayList();
        if (this.restTemplateProperties.isJacksonMessageConverter()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
                    new MappingJackson2HttpMessageConverter(objectMapper);
            messageConverters.add(mappingJackson2HttpMessageConverter);
        } else {
            FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
            fastJsonHttpMessageConverter.setSupportedMediaTypes(ImmutableList.of(MediaType.APPLICATION_JSON));
            FastJsonConfig fastJsonConfig = new FastJsonConfig();
            fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
            fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
            messageConverters.add(fastJsonHttpMessageConverter);
        }
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new ByteArrayHttpMessageConverter());

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setSupportedMediaTypes(ImmutableList.of(MediaType.valueOf("text/plain;charset=UTF-8")));
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        messageConverters.add(stringHttpMessageConverter);

        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setMessageConverters(messageConverters);

        boolean dayuSentinelEnabled = false;
        if (Objects.nonNull(environment)) {
            Boolean sentinelEnabled = environment.getProperty(DAYU_SENTINEL_ENABLED, Boolean.class);
            dayuSentinelEnabled = BooleanUtils.toBooleanDefaultIfNull(sentinelEnabled, false);
        }
        RestTemplateProperties.SentinelProperties sentinelProperties = restTemplateProperties.getSentinel();
        if (dayuSentinelEnabled && sentinelProperties.isEnabled()) {
            restTemplate.getInterceptors().add(0, sentinelHttpRequestInterceptor);
        }
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public StatsDClient statsDClient() {
        RestTemplateProperties.StatsdProperties statsdProperties = this.restTemplateProperties.getStatsd();
        return new NonBlockingStatsDClient(this.trimPrefix(statsdProperties.getPrefix()),
                statsdProperties.getHostname(), statsdProperties.getPort());
    }

    private String trimPrefix(String prefix) {
        String trimmedPrefix = (StringUtils.hasText(prefix) ? prefix : null);
        while (trimmedPrefix != null && trimmedPrefix.endsWith(".")) {
            trimmedPrefix = trimmedPrefix.substring(0, trimmedPrefix.length() - 1);
        }

        return trimmedPrefix;
    }

    @RefreshScope
    @Bean
    @ConditionalOnBean({RestTemplate.class, StatsDClient.class})
    @ConditionalOnMissingBean
    public SyncClientTemplate syncClientTemplate(RestTemplate restTemplate, StatsDClient statsDClient) {
        SyncClientTemplate syncClientTemplate = new SyncClientTemplate(restTemplate);
        syncClientTemplate.setProjName(this.buildProjName());
        syncClientTemplate.setStatsDClient(statsDClient);
        syncClientTemplate.setIgnoreLogMethodName(restTemplateProperties.getLog().getIgnoreMethodName());
        return syncClientTemplate;
    }

    private String buildProjName() {
        if (Objects.nonNull(environment)) {
            return environment.getProperty(SPRING_APPLICATION_NAME, restTemplateProperties.getProjName());
        }
        return restTemplateProperties.getProjName();
    }

    private void closeIdleExpiredConnections(PoolingHttpClientConnectionManager connectionManager) {
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1, LyThreadFactoryBuilderUtil.build("http" +
                "-client-idle"));
        service.scheduleAtFixedRate(() -> {
            try {
                connectionManager.closeExpiredConnections();
                connectionManager.closeIdleConnections(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("close expired/idle connections exception", e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
}
