package com.github.liaomengge.base_common.utils.serialize;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.github.liaomengge.base_common.utils.io.LyIOUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Created by liaomengge on 17/11/7.
 */
@Slf4j
@UtilityClass
public class LyProtoStuffSerializerUtil {

    /**
     * 序列化对象
     *
     * @param obj
     * @return
     */
    public <T> byte[] serialize(T obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = null;
        try {
            buffer = LinkedBuffer.allocate(1024 * 1024);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            log.error("serialize fail", e);
            return null;
        } finally {
            if (Objects.nonNull(buffer)) {
                buffer.clear();
            }
        }
    }

    /**
     * 反序列化对象
     *
     * @param paramArrayOfByte
     * @param targetClass
     * @return
     */
    public <T> T deserialize(byte[] paramArrayOfByte, Class<T> targetClass) {
        if (ArrayUtils.isEmpty(paramArrayOfByte)) {
            return null;
        }
        try {
            Schema<T> schema = RuntimeSchema.getSchema(targetClass);
            T newInstance = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(paramArrayOfByte, newInstance, schema);
            return newInstance;
        } catch (Exception e) {
            log.error("deserialize fail", e);
            return null;
        }
    }

    /**
     * 反序列化对象
     *
     * @param input
     * @param typeClass
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T deserialize(InputStream input, Class<T> typeClass) {
        if (Objects.isNull(input)) {
            return null;
        }
        try {
            Schema<T> schema = RuntimeSchema.getSchema(typeClass);
            T newInstance = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(input, newInstance, schema);
            return newInstance;
        } catch (Exception e) {
            log.error("deserialize fail", e);
            return null;
        }
    }

    /**
     * 序列化列表
     *
     * @param objList
     * @return
     */
    public <T> byte[] serializeList(List<T> objList) {
        if (CollectionUtils.isEmpty(objList)) {
            return null;
        }
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objList.get(0).getClass());
        LinkedBuffer buffer = null;
        ByteArrayOutputStream bos = null;
        try {
            buffer = LinkedBuffer.allocate(1024 * 1024);
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("serialize fail", e);
            return null;
        } finally {
            if (Objects.nonNull(buffer)) {
                buffer.clear();
            }
            LyIOUtil.closeQuietly(bos);
        }
    }

    /**
     * 反序列化列表
     *
     * @param paramArrayOfByte
     * @param targetClass
     * @return
     */
    public <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
        if (ArrayUtils.isEmpty(paramArrayOfByte)) {
            return null;
        }
        try {
            Schema<T> schema = RuntimeSchema.getSchema(targetClass);
            return ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
        } catch (Exception e) {
            log.error("deserialize fail", e);
            return null;
        }
    }
}