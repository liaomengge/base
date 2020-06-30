package cn.ly.base_common.support.objects;

import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * Created by liaomengge on 2019/11/27.
 */
@UtilityClass
public class _Objects {

    public <T> T defaultIfNull(T t, T defaultValue) {
        return Objects.isNull(t) ? defaultValue : t;
    }
}
