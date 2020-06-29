package cn.mwee.base_common.utils.serialize;

import org.apache.commons.lang3.ArrayUtils;
import org.nustaq.serialization.FSTConfiguration;

import java.io.Serializable;

/**
 * Created by liaomengge on 17/11/7.
 */
public final class FstSerializationUtil {

    private FstSerializationUtil() {
    }

    private final static FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();

    /**
     * 对象必须实现序列化
     *
     * @param obj
     * @return
     */
    public static <T extends Serializable> byte[] serialize(T obj) {
        byte[] result = null;
        if (null == obj) {
            return result;
        }

        try {
            result = configuration.asByteArray(obj);
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T deserialize(byte[] bytes) {
        T obj = null;
        if (ArrayUtils.isEmpty(bytes)) {
            return obj;
        }

        try {
            obj = (T) configuration.asObject(bytes);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }

}
