package cn.ly.base_common.retrofit;

import cn.ly.base_common.helper.retrofit.RetrofitHelper;
import cn.ly.base_common.helper.retrofit.api.RetrofitApi;
import cn.ly.base_common.helper.retrofit.client.interceptor.HttpHeaderInterceptor;
import cn.ly.base_common.helper.retrofit.client.interceptor.HttpLoggingInterceptor;
import cn.ly.base_common.helper.retrofit.client.interceptor.SentinelRetrofitInterceptor;
import cn.ly.base_common.helper.retrofit.factory.RetrofitFactory;
import cn.ly.base_common.retrofit.RetrofitProperties.HttpClientProperties;
import cn.ly.base_common.retrofit.RetrofitProperties.SentinelProperties;
import cn.ly.base_common.retrofit.RetrofitProperties.UrlHttpClientProperties;
import cn.ly.base_common.utils.number.MwNumberUtil;
import com.google.common.collect.ImmutableMap;
import com.timgroup.statsd.StatsDClient;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import retrofit2.Retrofit;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2019/3/1.
 */
@Configuration
@ConditionalOnClass({OkHttpClient.class, HttpLoggingInterceptor.class, Retrofit.class, RetrofitFactory.class})
@EnableConfigurationProperties(RetrofitProperties.class)
public class RetrofitAutoConfiguration {

    private static final String DAYU_SENTINEL_ENABLED = "ly.dayu.sentinel.enabled";
    private static final String SPRING_APPLICATION_NAME = "spring.application.name";

    @Autowired(required = false)
    private Environment environment;

    @Autowired
    private RetrofitProperties retrofitProperties;

    private OkHttpClient.Builder newBuilder(long readTimeout, long writeTime, long connectTime,
                                            ConnectionPool connectionPool,
                                            SentinelRetrofitInterceptor sentinelRetrofitInterceptor,
                                            HttpLoggingInterceptor httpLoggingInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTime, TimeUnit.MILLISECONDS)
                .connectTimeout(connectTime, TimeUnit.MILLISECONDS)
                .connectionPool(connectionPool)
                .addInterceptor(httpLoggingInterceptor);
        if (retrofitProperties.getHttp().isEnabledTraceHeader()) {
            builder.addInterceptor(new HttpHeaderInterceptor());
        }

        boolean dayuSentinelEnabled = false;
        if (Objects.nonNull(environment)) {
            Boolean sentinelEnabled = environment.getProperty(DAYU_SENTINEL_ENABLED, Boolean.class);
            dayuSentinelEnabled = BooleanUtils.toBooleanDefaultIfNull(sentinelEnabled, false);
        }
        SentinelProperties sentinelProperties = retrofitProperties.getSentinel();
        if (dayuSentinelEnabled && sentinelProperties.isEnabled()) {
            builder.interceptors().add(0, sentinelRetrofitInterceptor);
        }
        return builder;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(StatsDClient.class)
    public SentinelRetrofitInterceptor sentinelRetrofitInterceptor(StatsDClient statsDClient) {
        return new SentinelRetrofitInterceptor(statsDClient);
    }

    @RefreshScope
    @Bean
    @ConditionalOnBean(StatsDClient.class)
    public HttpLoggingInterceptor httpLoggingInterceptor(StatsDClient statsDClient) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(this.buildProjName());
        httpLoggingInterceptor.setStatsDClient(statsDClient);
        httpLoggingInterceptor.setIgnoreLogMethodName(this.retrofitProperties.getLog().getIgnoreMethodName());
        return httpLoggingInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionPool connectionPool() {
        HttpClientProperties httpClientProperties = this.retrofitProperties.getHttp();
        int maxIdleConnections = httpClientProperties.getMaxIdleConnections();
        long keepAlive = httpClientProperties.getKeepAlive();
        return new ConnectionPool(maxIdleConnections, keepAlive, TimeUnit.MINUTES);
    }

    @RefreshScope
    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient(ConnectionPool connectionPool,
                                     SentinelRetrofitInterceptor sentinelRetrofitInterceptor,
                                     HttpLoggingInterceptor httpLoggingInterceptor) {
        HttpClientProperties http = retrofitProperties.getHttp();
        return newBuilder(http.getReadTimeout(), http.getWriteTimeout(), http.getConnectTimeout(),
                connectionPool, sentinelRetrofitInterceptor, httpLoggingInterceptor).build();
    }

    @RefreshScope
    @Bean
    @ConditionalOnBean(OkHttpClient.class)
    @ConditionalOnMissingBean
    public RetrofitFactory retrofitFactory(OkHttpClient okHttpClient,
                                           ConnectionPool connectionPool,
                                           SentinelRetrofitInterceptor sentinelRetrofitInterceptor,
                                           HttpLoggingInterceptor httpLoggingInterceptor) {
        HttpClientProperties httpClientProperties = retrofitProperties.getHttp();
        RetrofitFactory retrofitFactory = new RetrofitFactory(retrofitProperties.getMessageConverter(), okHttpClient);
        List<UrlHttpClientProperties> urlHttpClientProperties = httpClientProperties.getUrls();
        if (CollectionUtils.isNotEmpty(urlHttpClientProperties)) {
            Map<String, OkHttpClient> okHttpClientMap =
                    urlHttpClientProperties.stream().collect(Collectors.toMap(UrlHttpClientProperties::getUrl,
                            val -> newBuilder(MwNumberUtil.getIntValue(val.getReadTimeout(),
                                    httpClientProperties.getReadTimeout()),
                                    MwNumberUtil.getIntValue(val.getWriteTimeout(),
                                            httpClientProperties.getWriteTimeout()),
                                    MwNumberUtil.getIntValue(val.getConnectTimeout(),
                                            httpClientProperties.getConnectTimeout()),
                                    connectionPool, sentinelRetrofitInterceptor, httpLoggingInterceptor).build(), (v1
                                    , v2) -> v2));
            retrofitFactory.setOkHttpClientMap(okHttpClientMap);
        }
        return retrofitFactory;
    }

    @Bean
    @ConditionalOnBean(RetrofitFactory.class)
    @ConditionalOnMissingBean
    public RetrofitApi retrofitApi(RetrofitFactory retrofitFactory) {
        return retrofitFactory.create();
    }

    @RefreshScope
    @Bean
    @ConditionalOnMissingBean
    public RetrofitHelper retrofitHelper() {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(retrofitProperties.getHttp().getRetryCount(),
                ImmutableMap.of(SocketTimeoutException.class, Boolean.TRUE));
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return new RetrofitHelper(retryTemplate);
    }

    private String buildProjName() {
        if (Objects.nonNull(environment)) {
            return environment.getProperty(SPRING_APPLICATION_NAME, retrofitProperties.getProjName());
        }
        return retrofitProperties.getProjName();
    }
}
