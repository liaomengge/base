package cn.ly.base_common.convert;

import cn.ly.base_common.convert.wrapper.FastJsonHttpMessageConvertWrapper;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
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
@Configuration
@ConditionalOnClass(FastJsonHttpMessageConverter.class)
@EnableConfigurationProperties(FastJsonConvertProperties.class)
public class FastJsonConvertAutoConfiguration {

    private final FastJsonConvertProperties fastJsonConvertProperties;

    public FastJsonConvertAutoConfiguration(FastJsonConvertProperties fastJsonConvertProperties) {
        this.fastJsonConvertProperties = fastJsonConvertProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConvertWrapper fastJsonHttpMessageConverter = new FastJsonHttpMessageConvertWrapper();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        if (Objects.nonNull(fastJsonConvertProperties)) {
            fastJsonConfig.setCharset(fastJsonConvertProperties.getCharset());
        }
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.BrowserSecure,
                SerializerFeature.DisableCircularReferenceDetect);
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(new MediaType("application", "json", fastJsonConfig.getCharset()));
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
        return new HttpMessageConverters(converter);
    }
}
