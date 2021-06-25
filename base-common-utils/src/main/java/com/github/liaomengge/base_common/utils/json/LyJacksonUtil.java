package com.github.liaomengge.base_common.utils.json;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
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
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@UtilityClass
public class LyJacksonUtil {

    @Getter
    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setLocale(Locale.CHINA);
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        objectMapper.setDateFormat(new SimpleDateFormat(LyJdk8DateUtil.DATETIME_PATTERN, Locale.CHINA));

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule simpleModule = new SimpleModule();

        simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(LyJdk8DateUtil.TIME_FORMATTER));
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(LyJdk8DateUtil.TIME_FORMATTER));

        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(LyJdk8DateUtil.DATE_FORMATTER));
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(LyJdk8DateUtil.DATE_FORMATTER));

        simpleModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(LyJdk8DateUtil.DATETIME_FORMATTER));
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(LyJdk8DateUtil.DATETIME_FORMATTER));

        objectMapper.registerModule(simpleModule);
    }

    @SneakyThrows
    public String bean2Json(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return objectMapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public <T> T json2Bean(String jsonStr, Class<T> clazz) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        if (clazz == String.class) {
            return (T) jsonStr;
        }
        return objectMapper.readValue(jsonStr, clazz);
    }

    @SneakyThrows
    public <T> T json2Bean(String jsonStr, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        if (String.class.equals(typeReference.getType())) {
            return (T) jsonStr;
        }
        return objectMapper.readValue(jsonStr, typeReference);
    }

    public <T> T obj2Bean(Object obj, Class<T> clazz) {
        return objectMapper.convertValue(obj, clazz);
    }

    public <T> T obj2Bean(Object obj, TypeReference<T> typeReference) {
        return objectMapper.convertValue(obj, typeReference);
    }

    /************************************************华丽的分割线*******************************************************/

    public boolean isJson(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return false;
        }
        try {
            objectMapper.readValue(jsonStr, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String toJson(Object object) {
        return bean2Json(object);
    }

    public Object fromJson(String jsonStr) {
        return json2Bean(jsonStr, Object.class);
    }

    public <T> T fromJson(String jsonStr, Class<T> clazz) {
        return json2Bean(jsonStr, clazz);
    }

    public <T> T fromJson(String jsonStr, TypeReference<T> typeReference) {
        return json2Bean(jsonStr, typeReference);
    }
}
