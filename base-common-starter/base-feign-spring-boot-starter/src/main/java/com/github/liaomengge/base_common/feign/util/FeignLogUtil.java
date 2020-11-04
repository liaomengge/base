package com.github.liaomengge.base_common.feign.util;

import com.github.liaomengge.base_common.feign.FeignProperties;
import com.google.common.collect.Iterables;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2020/11/2.
 */
@UtilityClass
public class FeignLogUtil {

    public boolean isIgnoreLogHeader(String methodName, FeignProperties feignProperties) {
        String ignoreHeaderMethodName = feignProperties.getLogger().getIgnoreHeaderMethodName();
        if (StringUtils.equalsIgnoreCase(ignoreHeaderMethodName, "*")) {
            return true;
        }
        if (StringUtils.isNotBlank(ignoreHeaderMethodName)) {
            Iterable<String> iterable = SPLITTER.split(ignoreHeaderMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    public boolean isIgnoreLogRequest(String methodName, FeignProperties feignProperties) {
        String ignoreArgsMethodName = feignProperties.getLogger().getIgnoreRequestMethodName();
        if (StringUtils.isNotBlank(ignoreArgsMethodName)) {
            Iterable<String> iterable = SPLITTER.split(ignoreArgsMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    public boolean isIgnoreLogResponse(String methodName, FeignProperties feignProperties) {
        String ignoreResultMethodName = feignProperties.getLogger().getIgnoreResponseMethodName();
        if (StringUtils.isNotBlank(ignoreResultMethodName)) {
            Iterable<String> iterable = SPLITTER.split(ignoreResultMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }
}
