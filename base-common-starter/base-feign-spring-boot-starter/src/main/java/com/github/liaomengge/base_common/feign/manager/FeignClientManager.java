package com.github.liaomengge.base_common.feign.manager;

import com.github.liaomengge.base_common.feign.FeignProperties;
import com.github.liaomengge.base_common.feign.helper.FeignHelper;
import com.github.liaomengge.base_common.feign.pojo.FeignTarget;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;
import org.springframework.cloud.openfeign.FeignClient;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by liaomengge on 2020/12/17.
 */
public class FeignClientManager {

    @Getter
    private final Map<String, FeignTarget> feignTargetMap = Maps.newConcurrentMap();

    private final FeignHelper feignHelper;
    private final FeignProperties feignProperties;

    public FeignClientManager(FeignHelper feignHelper, FeignProperties feignProperties) {
        this.feignHelper = feignHelper;
        this.feignProperties = feignProperties;
    }

    @PostConstruct
    public void init() {
        List<String> basePackages = feignProperties.getBasePackages();
        if (CollectionUtils.isEmpty(basePackages)) {
            return;
        }
        scanFeignClient(basePackages);
    }

    private void scanFeignClient(List<String> basePackages) {
        Reflections reflections = new Reflections(basePackages);
        Set<Class<?>> feignClientClassSet = reflections.getTypesAnnotatedWith(FeignClient.class);
        if (CollectionUtils.isEmpty(feignClientClassSet)) {
            return;
        }
        for (Class<?> clazz : feignClientClassSet) {
            FeignClient feignClient = clazz.getDeclaredAnnotation(FeignClient.class);
            if (Objects.isNull(feignClient)) {
                continue;
            }
            FeignTarget feignTarget = feignHelper.buildFeignTarget(feignClient);
            feignTarget.setType(clazz);
            feignTargetMap.putIfAbsent(feignTarget.getName(), feignTarget);
        }
    }
}
