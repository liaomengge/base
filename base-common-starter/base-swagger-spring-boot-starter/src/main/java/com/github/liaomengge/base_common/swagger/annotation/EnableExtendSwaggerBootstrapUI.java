package com.github.liaomengge.base_common.swagger.annotation;

import com.github.liaomengge.base_common.swagger.security.ExtendSecurityConfiguration;

import java.lang.annotation.*;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/7/12.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ExtendSecurityConfiguration.class)
@ComponentScan(basePackages = {"com.github.xiaoymin.knife4j.spring.plugin", "com.github.xiaoymin.knife4j.spring.web"})
public @interface EnableExtendSwaggerBootstrapUI {
}
