package com.github.liaomengge.base_common.framework.annotation;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.*;

/**
 * Created by liaomengge on 2020/11/9.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@BaseSpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public @interface EnableBaseFramework {
}
