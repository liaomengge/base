package com.github.liaomengge.base_common.feign.sentinel.initializer;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
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
 * Created by liaomengge on 2020/12/11.
 */
public class FeignSentinelInitializer implements InitializingBean {

    @Getter
    private final Map<String, Set<String>> sentinelResourceMap = Maps.newConcurrentMap();

    private final FeignClientManager feignClientManager;

    public FeignSentinelInitializer(FeignClientManager feignClientManager) {
        this.feignClientManager = feignClientManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, FeignTarget> feignTargetMap = feignClientManager.getFeignTargetMap();
        Map<String, Set<String>> resourceMap = buildFeignSentinelResource(feignTargetMap);
        sentinelResourceMap.putAll(resourceMap);
    }

    private Map<String, Set<String>> buildFeignSentinelResource(Map<String, FeignTarget> feignTargetMap) {
        return feignTargetMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> {
                    FeignTarget feignTarget = entry.getValue();
                    Method[] methods = ReflectionUtils.getDeclaredMethods(feignTarget.getType());
                    return Arrays.stream(methods).filter(method -> !ReflectionUtils.isObjectMethod(method))
                            .map(method -> SentinelContractHolder.METADATA_MAP.get(feignTarget.getType().getName() + Feign.configKey(feignTarget.getType(), method)))
                            .filter(Objects::nonNull)
                            .map(methodMetadata -> methodMetadata.template().method().toUpperCase() + ":" + feignTarget.getTargetUrl() + methodMetadata.template().path()).collect(Collectors.toSet());
                }, (v1, v2) -> v2));
    }

}

