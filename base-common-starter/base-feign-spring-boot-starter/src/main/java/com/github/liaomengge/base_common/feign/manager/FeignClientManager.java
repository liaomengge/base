package com.github.liaomengge.base_common.feign.manager;

import com.github.liaomengge.base_common.feign.FeignProperties;
import com.github.liaomengge.base_common.feign.helper.FeignHelper;
import com.github.liaomengge.base_common.feign.pojo.FeignTarget;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2020/12/17.
 */
public class FeignClientManager implements BeanFactoryAware {

    @Getter
    private final Map<String, FeignTarget> feignTargetMap = Maps.newConcurrentMap();

    private BeanFactory beanFactory;

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
            basePackages = AutoConfigurationPackages.get(this.beanFactory);
            List<String> enableFeignClientsPackages = getEnableFeignClientsPackages(basePackages);
            if (CollectionUtils.isNotEmpty(enableFeignClientsPackages)) {
                basePackages = enableFeignClientsPackages;
            }
        }
        if (CollectionUtils.isEmpty(basePackages)) {
            return;
        }
        scanFeignClient(basePackages.stream().distinct().collect(Collectors.toList()));
    }

    private List<String> getEnableFeignClientsPackages(List<String> basePackages) {
        List<String> enableFeignClientsPackages = Lists.newArrayList();
        Reflections reflections = new Reflections(basePackages);
        Set<Class<?>> enableFeignClientsClassSet = reflections.getTypesAnnotatedWith(EnableFeignClients.class);
        if (CollectionUtils.isNotEmpty(enableFeignClientsClassSet)) {
            for (Class<?> clazz : enableFeignClientsClassSet) {
                EnableFeignClients enableFeignClients = AnnotationUtils.findAnnotation(clazz, EnableFeignClients.class);
                if (Objects.isNull(enableFeignClients)) {
                    continue;
                }
                String[] pkgs = enableFeignClients.basePackages();
                if (ArrayUtils.isNotEmpty(pkgs)) {
                    enableFeignClientsPackages.addAll(Arrays.asList(pkgs));
                }
                Class<?>[] pkgClasses = enableFeignClients.basePackageClasses();
                if (ArrayUtils.isNotEmpty(pkgClasses)) {
                    for (Class<?> pkgClazz : pkgClasses) {
                        enableFeignClientsPackages.add(ClassUtils.getPackageName(pkgClazz));
                    }
                }
            }
        }
        return enableFeignClientsPackages;
    }

    private void scanFeignClient(List<String> basePackages) {
        Reflections reflections = new Reflections(basePackages);
        Set<Class<?>> feignClientClassSet = reflections.getTypesAnnotatedWith(FeignClient.class);
        if (CollectionUtils.isEmpty(feignClientClassSet)) {
            return;
        }
        for (Class<?> clazz : feignClientClassSet) {
            FeignClient feignClient = AnnotationUtils.findAnnotation(clazz, FeignClient.class);
            if (Objects.isNull(feignClient)) {
                continue;
            }
            FeignTarget feignTarget = feignHelper.buildFeignTarget(feignClient);
            feignTarget.setType(clazz);
            feignTargetMap.putIfAbsent(feignTarget.getName(), feignTarget);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
