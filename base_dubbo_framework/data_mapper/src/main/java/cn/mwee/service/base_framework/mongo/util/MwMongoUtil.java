package cn.mwee.service.base_framework.mongo.util;

import cn.mwee.base_common.utils.collection.MwMapUtil;
import cn.mwee.service.base_framework.mongo.domain.BaseMongoDoc;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;

/**
 * Created by liaomengge on 17/3/2.
 */
public final class MwMongoUtil {

    public static <T extends BaseMongoDoc> Update convertObjectToParams(T obj, String... excludePropertyName) {
        Map<String, Object> params = MwMapUtil.bean2Map(obj);

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

    public static Update convertMapToParams(Map<String, Object> map, String...
            excludePropertyName) {
        Update update = new Update();
        map.entrySet().stream().filter(entry -> {
            String key = entry.getKey();
            return !"_id".equalsIgnoreCase(key) || !ArrayUtils.contains(excludePropertyName, key);
        }).forEach(entry -> update.set(entry.getKey(), entry.getValue()));

        return update;
    }
}
