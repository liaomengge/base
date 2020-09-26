package cn.ly.base_common.convert.wrapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.base.Charsets;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

import lombok.NonNull;

/**
 * Created by liaomengge on 2018/12/10.
 */
@NonNull
public class FastJsonHttpMessageConvertWrapper extends FastJsonHttpMessageConverter {

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        return this.readByType(getType(type, contextClass), inputMessage);
    }

    private Object readByType(Type type, HttpInputMessage inputMessage) throws IOException {
        try {
            InputStream in = inputMessage.getBody();
            return JSON.parseObject(in, Charsets.UTF_8, type, getFastJsonConfig().getFeatures());
        } catch (JSONException ex) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex.getMessage(), ex, inputMessage);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("I/O error while reading input message", ex, inputMessage);
        }
    }
}
