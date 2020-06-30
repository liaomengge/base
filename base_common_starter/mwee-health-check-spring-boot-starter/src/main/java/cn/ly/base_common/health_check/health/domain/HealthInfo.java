package cn.ly.base_common.health_check.health.domain;

import cn.mwee.base_common.utils.error.MwThrowableUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.curator.shaded.com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by liaomengge on 2019/7/11.
 */
@Data
@Accessors(chain = true)
public class HealthInfo {

    private Status status;
    private Map<String, Object> details;

    public HealthInfo() {
        this.status = Status.UP;
    }

    public HealthInfo(Status status) {
        this.status = status;
    }

    public static HealthInfo up() {
        return new HealthInfo();
    }

    public static HealthInfo down() {
        return new HealthInfo(Status.DOWN);
    }

    public HealthInfo withDetail(String key, Object value) {
        this.setDetails(ImmutableMap.of(key, value));
        return this;
    }

    public HealthInfo withMetaData(Object value) {
        return withDetail("MetaData", value);
    }

    public HealthInfo withException(Exception e) {
        return withDetail("Exception", MwThrowableUtil.getStackTrace(e));
    }

    public enum Status {
        UP, DOWN
    }
}
