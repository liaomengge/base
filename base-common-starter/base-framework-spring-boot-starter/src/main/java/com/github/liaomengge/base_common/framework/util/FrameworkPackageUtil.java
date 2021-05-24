package com.github.liaomengge.base_common.framework.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Created by liaomengge on 2021/5/24.
 */
@UtilityClass
public class FrameworkPackageUtil {

    @Getter
    @Setter
    private static List<String> basePackages = null;
}
