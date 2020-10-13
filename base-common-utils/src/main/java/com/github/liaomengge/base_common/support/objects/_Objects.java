package com.github.liaomengge.base_common.support.objects;

import java.util.Objects;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/11/27.
 */
@UtilityClass
public class _Objects {

    public <T> T defaultIfNull(T t, T defaultValue) {
        return Objects.isNull(t) ? defaultValue : t;
    }
}
