package com.github.liaomengge.base_common.fastjson;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.liaomengge.base_common.fastjson.wrapper.FastJsonHttpMessageConvertWrapper;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by liaomengge on 2018/12/10.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(FastJsonHttpMessageConverter.class)
@EnableConfigurationProperties(FastJsonProperties.class)
public class FastJsonAutoConfiguration {

    private final FastJsonProperties fastJsonProperties;

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConvertWrapper fastJsonHttpMessageConverter = new FastJsonHttpMessageConvertWrapper();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        if (Objects.nonNull(fastJsonProperties)) {
            fastJsonConfig.setCharset(fastJsonProperties.getCharset());
        }
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(new MediaType("application", "json", fastJsonConfig.getCharset()));
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
        return new HttpMessageConverters(converter);
    }
}
