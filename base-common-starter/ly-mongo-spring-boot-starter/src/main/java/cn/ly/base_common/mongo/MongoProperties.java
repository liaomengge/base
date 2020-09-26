package cn.ly.base_common.mongo;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * Created by liaomengge on 2018/11/7.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "ly.mongo")
public class MongoProperties {

    @NotNull
    private String[] basePackages;
    @NotNull
    private String uri;
}
