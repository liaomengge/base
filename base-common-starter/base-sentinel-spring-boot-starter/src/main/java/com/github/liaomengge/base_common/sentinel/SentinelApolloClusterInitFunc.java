package com.github.liaomengge.base_common.sentinel;

import com.alibaba.csp.sentinel.init.InitFunc;
import com.github.liaomengge.base_common.sentinel.handler.AbstractSentinelHandler;
import com.github.liaomengge.base_common.support.loader.ExtReflectionLoader;

import java.util.Map;

/**
 * Created by liaomengge on 2021/1/12.
 */
public class SentinelApolloClusterInitFunc implements InitFunc {

    @Override
    public void init() throws Exception {
        Map<String, AbstractSentinelHandler> sentinelInitializerMap =
                ExtReflectionLoader.getLoader(AbstractSentinelHandler.class).getInstanceMap();
        sentinelInitializerMap.values().stream().forEach(AbstractSentinelHandler::init);
    }
}
