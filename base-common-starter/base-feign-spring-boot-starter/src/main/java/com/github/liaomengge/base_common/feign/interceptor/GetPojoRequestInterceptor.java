package com.github.liaomengge.base_common.feign.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.*;

/**
 * Created by liaomengge on 2020/8/25.
 */
@Slf4j
@AllArgsConstructor
public class GetPojoRequestInterceptor implements RequestInterceptor, Ordered {
    
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
                if (StringUtils.isNoneBlank(path)) {
                    buildQuery(entry.getValue(), path + "." + entry.getKey(), queries);
                } else {
                    buildQuery(entry.getValue(), entry.getKey(), queries);
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
