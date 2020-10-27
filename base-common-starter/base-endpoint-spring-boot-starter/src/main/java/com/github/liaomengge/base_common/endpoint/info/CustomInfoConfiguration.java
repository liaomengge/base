package com.github.liaomengge.base_common.endpoint.info;

import com.github.liaomengge.base_common.endpoint.condition.ConditionalOnCustomProperty;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.Properties;

/**
 * Created by liaomengge on 2019/7/4.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnCustomProperty
@ConditionalOnProperty(prefix = "base.endpoint.info", name = "enabled", matchIfMissing = true)
public class CustomInfoConfiguration {

    @Bean
    public BuildProperties buildProperties(ApplicationContext context) {
        Properties properties = new Properties();
        String version = context.getBeansWithAnnotation(SpringBootApplication.class).entrySet().stream()
                .findFirst()
                .flatMap(es -> {
                    String implementationVersion = es.getValue().getClass().getPackage().getImplementationVersion();
                    return Optional.ofNullable(implementationVersion);
                }).orElse("unknown");
        properties.setProperty("version", version);
        properties.setProperty("time", LyJdk8DateUtil.getNowDate2String());
        return new BuildProperties(properties);
    }

    @Bean
    public CustomInfoContributor customInfoContributor() {
        return new CustomInfoContributor();
    }
}
