package cn.ly.base_common.mongo;

import com.mongodb.MongoClientURI;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

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

    public MongoClientURI createMongoClientURI() {
        return new MongoClientURI(uri);
    }
}
