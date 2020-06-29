package cn.ly.base_common.utils.serialize;

import cn.ly.base_common.utils.io.MwIOUtil;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by liaomengge on 17/11/7.
 */
@UtilityClass
public class ProtoStuffSerializerUtil {

    /**
     * 序列化对象
     *
     * @param obj
     * @return
     */
    public <T> byte[] serialize(T obj) {
        byte[] protostuff = null;
        if (obj == null) {
            return protostuff;
        }
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
        try {
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            return null;
        } finally {
            buffer.clear();
        }
        return protostuff;
    }

    /**
     * 反序列化对象
     *
     * @param paramArrayOfByte
     * @param targetClass
     * @return
     */
    public <T> T deserialize(byte[] paramArrayOfByte, Class<T> targetClass) {
        T instance = null;
        if (ArrayUtils.isEmpty(paramArrayOfByte)) {
            return instance;
        }

        try {
            instance = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        Schema<T> schema = RuntimeSchema.getSchema(targetClass);
        ProtostuffIOUtil.mergeFrom(paramArrayOfByte, instance, schema);
        return instance;
    }

    /**
     * 序列化列表
     *
     * @param objList
     * @return
     */
    public <T> byte[] serializeList(List<T> objList) {
        byte[] protostuff = null;
        if (CollectionUtils.isEmpty(objList)) {
            return protostuff;
        }

        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objList.get(0).getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
            protostuff = bos.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            buffer.clear();
            MwIOUtil.closeQuietly(bos);
        }

        return protostuff;
    }

    /**
     * 反序列化列表
     *
     * @param paramArrayOfByte
     * @param targetClass
     * @return
     */
    public <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
        List<T> result = null;
        if (ArrayUtils.isEmpty(paramArrayOfByte)) {
            return result;
        }

        Schema<T> schema = RuntimeSchema.getSchema(targetClass);

        try {
            result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
        } catch (IOException e) {
            return null;
        }
        return result;
    }
}