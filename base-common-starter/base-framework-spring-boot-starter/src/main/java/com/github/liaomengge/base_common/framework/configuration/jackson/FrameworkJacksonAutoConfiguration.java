package com.github.liaomengge.base_common.framework.configuration.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.github.liaomengge.base_common.framework.configuration.xss.serializer.XssJacksonDeserializer;
import com.github.liaomengge.base_common.framework.configuration.xss.serializer.XssJacksonSerializer;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Created by liaomengge on 2019/1/25.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class)
public class FrameworkJacksonAutoConfiguration {

    private final Environment environment;
    
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.setLocale(Locale.CHINA);
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        objectMapper.setDateFormat(new SimpleDateFormat(LyJdk8DateUtil.DATETIME_PATTERN, Locale.CHINA));

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule simpleModule = new SimpleModule();

        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(LyJdk8DateUtil.TIME_FORMATTER));
        simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(LyJdk8DateUtil.TIME_FORMATTER));

        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(LyJdk8DateUtil.DATE_FORMATTER));
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(LyJdk8DateUtil.DATE_FORMATTER));

        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(LyJdk8DateUtil.DATETIME_FORMATTER));
        simpleModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(LyJdk8DateUtil.DATETIME_FORMATTER));

        if (Objects.nonNull(environment)) {
            Boolean xxsEnabled = environment.getProperty("base.framework.xss.enabled", Boolean.class);
            if (BooleanUtils.toBoolean(xxsEnabled)) {
                simpleModule.addSerializer(String.class, new XssJacksonSerializer());
                simpleModule.addDeserializer(String.class, new XssJacksonDeserializer());
            }
        }

        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }
}
