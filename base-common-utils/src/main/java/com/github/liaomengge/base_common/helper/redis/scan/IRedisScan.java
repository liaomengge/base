package com.github.liaomengge.base_common.helper.redis.scan;

import java.util.List;
import java.util.Map;

/**
 * Created by liaomengge on 17/12/13.
 */
@FunctionalInterface
public interface IRedisScan {
    void doHandle(List<Map<String, String>> mapList);
}
