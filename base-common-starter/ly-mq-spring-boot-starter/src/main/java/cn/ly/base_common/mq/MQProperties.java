package cn.ly.base_common.mq;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2019/5/6.
 */
@Data
@ConfigurationProperties(prefix = "ly.mq")
public class MQProperties {

    private String type;
}
