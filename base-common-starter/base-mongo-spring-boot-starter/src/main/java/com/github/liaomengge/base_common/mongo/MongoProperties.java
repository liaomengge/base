package com.github.liaomengge.base_common.mongo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Created by liaomengge on 2018/11/7.
 */
@Data
@Validated
@ConfigurationProperties("base.mongo")
public class MongoProperties {

    @NotNull
    private String[] basePackages;
    @NotNull
    private String uri;
}
