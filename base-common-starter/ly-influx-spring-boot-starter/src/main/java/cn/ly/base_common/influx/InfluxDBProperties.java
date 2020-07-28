package cn.ly.base_common.influx;

import cn.ly.base_common.influx.consts.InfluxConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * Created by liaomengge on 2020/7/21.
 */
@Data
@Validated
@ConfigurationProperties("spring.influx")
public class InfluxDBProperties {

    private String db;
    private String url;
    private String user;
    private String password;
    private String policy;
    private boolean newVersionEnabled;

    public String getDb() {
        return StringUtils.hasText(this.db) ? this.db : InfluxConst.DEFAULT_DATABASE;
    }

    public String getPolicy() {
        return StringUtils.hasText(this.policy) ? this.policy : "autogen";
    }
}
