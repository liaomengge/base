package com.github.liaomengge.base_common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by liaomengge on 2019/1/25.
 */
@Configuration
@AutoConfigureAfter(org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class)
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnBean(ObjectMapper.class)
@ConditionalOnProperty(name = "spring.http.converters.preferred-json-mapper", havingValue = "jackson",
        matchIfMissing = true)
public class JacksonAutoConfiguration {

    @Autowired(required = false)
    private JacksonProperties jacksonProperties;

    @Bean
    @ConditionalOnMissingBean(MappingJackson2HttpMessageConverter.class)
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
            ObjectMapper objectMapper) {
        if (Objects.nonNull(jacksonProperties)) {
            Map<SerializationFeature, Boolean> serializationMap = jacksonProperties.getSerialization();
            Optional.ofNullable(serializationMap).ifPresent(map -> map.forEach((key, value) -> objectMapper.configure(key, value)));

            Map<DeserializationFeature, Boolean> deserializationMap = jacksonProperties.getDeserialization();
            Optional.ofNullable(deserializationMap).ifPresent(map -> map.forEach((key, value) -> objectMapper.configure(key, value)));

            Map<MapperFeature, Boolean> mapperMap = jacksonProperties.getMapper();
            Optional.ofNullable(mapperMap).ifPresent(map -> map.forEach((key, value) -> objectMapper.configure(key,
                    value)));

            Map<JsonGenerator.Feature, Boolean> generatorMap = jacksonProperties.getGenerator();
            Optional.ofNullable(generatorMap).ifPresent(map -> map.forEach((key, value) -> objectMapper.configure(key
                    , value)));

            Map<JsonParser.Feature, Boolean> parserMap = jacksonProperties.getParser();
            Optional.ofNullable(parserMap).ifPresent(map -> map.forEach((key, value) -> objectMapper.configure(key,
                    value)));
        }
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
