package com.github.liaomengge.service.base_framework.helper;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2021/2/18.
 */
@Component
@AllArgsConstructor
public class EndpointHelper {

    private final WebEndpointDiscoverer webEndpointDiscoverer;

    public List<String> discoverEndpoints() {
        return Optional.ofNullable(webEndpointDiscoverer)
                .map(val -> val.getEndpoints()
                        .stream()
                        .map(ExposableWebEndpoint::getEndpointId)
                        .map(EndpointId::toString)
                        .distinct()
                        .collect(Collectors.toList()))
                .orElse(Lists.newArrayList());
    }
}
