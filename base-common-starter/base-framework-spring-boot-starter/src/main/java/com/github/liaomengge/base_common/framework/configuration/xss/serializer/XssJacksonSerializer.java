package com.github.liaomengge.base_common.framework.configuration.xss.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;

/**
 * Created by liaomengge on 2020/10/17.
 */
public class XssJacksonSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(StringEscapeUtils.escapeHtml4(s));
    }
}
