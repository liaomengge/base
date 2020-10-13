package com.github.liaomengge.base_common.feign.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by liaomengge on 2020/8/25.
 */
@AllArgsConstructor
public class GetPojoRequestInterceptor implements RequestInterceptor {

    private static final Logger log = LyLogger.getInstance(GetPojoRequestInterceptor.class);

    private final ObjectMapper objectMapper;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (HttpMethod.GET.name().equals(requestTemplate.method()) && null != requestTemplate.body()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(requestTemplate.body());
                requestTemplate.body(null, Util.UTF_8);

                Map<String, Collection<String>> queries = new HashMap<>();
                buildQuery(jsonNode, "", queries);
                requestTemplate.queries(queries);
            } catch (IOException e) {
                log.error("create get pojo request interceptor exception", e);
            }
        }
    }

    private void buildQuery(JsonNode jsonNode, String path, Map<String, Collection<String>> queries) {
        if (!jsonNode.isContainerNode()) {
            if (jsonNode.isNull()) {
                return;
            }
            Collection<String> values = queries.get(path);
            if (null == values) {
                values = new ArrayList<>();
                queries.put(path, values);
            }
            values.add(jsonNode.asText());
            return;
        }
        if (jsonNode.isArray()) {
            Iterator<JsonNode> it = jsonNode.elements();
            while (it.hasNext()) {
                buildQuery(it.next(), path, queries);
            }
        } else {
            Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (StringUtils.hasText(path)) {
                    buildQuery(entry.getValue(), path + "." + entry.getKey(), queries);
                } else {
                    buildQuery(entry.getValue(), entry.getKey(), queries);
                }
            }
        }
    }
}
