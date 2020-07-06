package cn.ly.base_common.utils.json;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Map;

@UtilityClass
public class LyJacksonUtil {

    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public String bean2Json(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public <T> T json2Bean(String jsonStr, Class<T> objClass) throws IOException {
        return objectMapper.readValue(jsonStr, objClass);
    }

    public <T> T json2Bean(String jsonStr, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(jsonStr, typeReference);
    }

    public <T, K, V> T map2Bean(Map<K, V> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    public <T, K, V> T map2Bean(Map<K, V> map, TypeReference<T> typeReference) {
        return objectMapper.convertValue(map, typeReference);
    }

    public <K, V> Map<K, V> bean2Map(Object obj) {
        return objectMapper.convertValue(obj, new TypeReference<Map<K, V>>() {
        });
    }

    public <K, V> Map<K, V> bean2Map(Object obj, TypeReference<Map<K, V>> typeReference) {
        return objectMapper.convertValue(obj, typeReference);
    }
}
