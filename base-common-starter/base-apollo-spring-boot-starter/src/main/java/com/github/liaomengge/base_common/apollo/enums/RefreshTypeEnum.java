package com.github.liaomengge.base_common.apollo.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by liaomengge on 2021/1/29.
 */
public enum RefreshTypeEnum {

    PROPERTIES,
    SCOPE,
    ALL;

    public static RefreshTypeEnum getInstance(String name) {
        return Arrays.stream(values())
                .filter(val -> StringUtils.equalsIgnoreCase(val.name(), name))
                .findFirst().orElse(RefreshTypeEnum.PROPERTIES);
    }
}
