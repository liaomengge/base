package cn.ly.base_common.helper.retrofit.factory;

import cn.ly.base_common.helper.retrofit.api.RetrofitApi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by liaomengge on 2019/3/1.
 */
public class RetrofitFactory {

    private static final String BASE_URL = "https://square.github.io/retrofit/";

    private final String messageConverter;
    private final OkHttpClient okHttpClient;
    @Setter
    private Map<String, OkHttpClient> okHttpClientMap;

    public RetrofitFactory(OkHttpClient okHttpClient) {
        this("fastjson", okHttpClient);
    }

    public RetrofitFactory(String messageConverter, OkHttpClient okHttpClient) {
        this.messageConverter = messageConverter;
        this.okHttpClient = okHttpClient;
    }

    public RetrofitApi create() {
        return create(BASE_URL, RetrofitApi.class);
    }

    public <T> T create(String url, Class<T> cls) {
        return newBuilder(toBaseUrl(url)).build().create(cls);
    }

    /**
     * using {@link RetrofitFactory#create(String, Class)} to replace
     *
     * @param url
     * @param cls
     * @param <T>
     * @return
     */
    @Deprecated
    public <T> T newInstance(String url, Class<T> cls) {
        return newBuilder(toBaseUrl(url)).build().create(cls);
    }

    public Retrofit.Builder newBuilder(String baseUrl) {
        if (MapUtils.isNotEmpty(okHttpClientMap)) {
            return newBuilder(okHttpClientMap.getOrDefault(StringUtils.trimToEmpty(baseUrl), okHttpClient), baseUrl);
        }
        return newBuilder(okHttpClient, baseUrl);
    }

    public Retrofit.Builder newBuilder(OkHttpClient okHttpClient, String baseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create());
        if (isJacksonMessageConverter()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            builder.addConverterFactory(JacksonConverterFactory.create(objectMapper));
        } else {
            builder.addConverterFactory(FastJsonConverterFactory.create());
        }
        return builder;
    }

    private String toBaseUrl(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url不能为空");
        }

        if (!url.endsWith("/")) {
            url = url + "/";
        }
        return url;
    }

    private boolean isJacksonMessageConverter() {
        return StringUtils.endsWithIgnoreCase("jackson", this.messageConverter);
    }
}
