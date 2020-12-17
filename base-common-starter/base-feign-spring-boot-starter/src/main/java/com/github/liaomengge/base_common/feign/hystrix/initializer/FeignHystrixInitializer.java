package com.github.liaomengge.base_common.feign.hystrix.initializer;

import com.github.liaomengge.base_common.feign.manager.FeignClientManager;
import com.github.liaomengge.base_common.feign.pojo.FeignTarget;
import com.google.common.collect.Maps;
import feign.Feign;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2020/12/17.
 */
public class FeignHystrixInitializer implements InitializingBean {

    @Getter
    private final Map<String, Set<String>> hystrixCommandKeyMap = Maps.newConcurrentMap();

    private final FeignClientManager feignClientManager;

    public FeignHystrixInitializer(FeignClientManager feignClientManager) {
        this.feignClientManager = feignClientManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, FeignTarget> feignTargetMap = feignClientManager.getFeignTargetMap();
        Map<String, Set<String>> commandKeyMap = buildFeignHystrixCommandKey(feignTargetMap);
        hystrixCommandKeyMap.putAll(commandKeyMap);
    }

    private Map<String, Set<String>> buildFeignHystrixCommandKey(Map<String, FeignTarget> feignTargetMap) {
        return feignTargetMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> {
                    FeignTarget feignTarget = entry.getValue();
                    Method[] methods = ReflectionUtils.getDeclaredMethods(feignTarget.getType());
                    return Arrays.stream(methods).filter(method -> !ReflectionUtils.isObjectMethod(method))
                            .map(method -> Feign.configKey(feignTarget.getType(), method))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                }, (v1, v2) -> v2));
    }
}
