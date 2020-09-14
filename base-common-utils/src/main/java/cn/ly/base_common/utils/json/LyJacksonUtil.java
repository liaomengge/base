package cn.ly.base_common.utils.json;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class LyJacksonUtil {

    @Getter
    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
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
    public <T> T json2Bean(String jsonStr, Class<T> clz) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        if (clz == String.class) {
            return (T) jsonStr;
        }
        return objectMapper.readValue(jsonStr, clz);
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

    public <T> T obj2Bean(Object obj, Class<T> clz) {
        return objectMapper.convertValue(obj, clz);
    }

    public <T> T obj2Bean(Object obj, TypeReference<T> typeReference) {
        return objectMapper.convertValue(obj, typeReference);
    }

    /*****************************************************华丽分分割线*************************************************/

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

    public <T> T fromJson(String jsonStr, Class<T> clz) {
        return json2Bean(jsonStr, clz);
    }

    public <T> T fromJson(String jsonStr, TypeReference<T> typeReference) {
        return json2Bean(jsonStr, typeReference);
    }
}
