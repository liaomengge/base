package com.github.liaomengge.service.base_framework.mongo.util;

import com.github.liaomengge.base_common.utils.collection.LyMapUtil;
import com.github.liaomengge.service.base_framework.mongo.domain.BaseMongoDoc;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;

/**
 * Created by liaomengge on 17/3/2.
 */
@UtilityClass
public class LyMongoUtil {

    public <T extends BaseMongoDoc> Update convertObjectToParams(T obj, String... excludePropertyName) {
        Map<String, Object> params = LyMapUtil.bean2Map(obj);

        Update update = new Update();
        String key;
        Object value;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            key = entry.getKey();
            if (!"_id".equalsIgnoreCase(key) || !ArrayUtils.contains(excludePropertyName, key)) {
                value = entry.getValue();
                update.set(key, value);
            }
        }

        return update;
    }

    public Update convertMapToParams(Map<String, Object> map, String...
            excludePropertyName) {
        Update update = new Update();
        map.entrySet().stream().filter(entry -> {
            String key = entry.getKey();
            return !"_id".equalsIgnoreCase(key) || !ArrayUtils.contains(excludePropertyName, key);
        }).forEach(entry -> update.set(entry.getKey(), entry.getValue()));

        return update;
    }
}
