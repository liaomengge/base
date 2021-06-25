package com.github.liaomengge.service.base_framework.common.filter;

import com.alibaba.dubbo.rpc.Filter;
import com.github.liaomengge.base_common.support.logger.JsonLogger;
import com.github.liaomengge.service.base_framework.common.config.FilterConfig;
import com.github.liaomengge.service.base_framework.common.config.ServiceConfig;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Setter;

/**
 * Created by liaomengge on 16/7/11.
 */
public abstract class AbstractFilter implements Filter {

    protected final static JsonLogger log = JsonLogger.getInstance(AbstractFilter.class);

    protected static final String SKIP_METHOD = "ping";

    protected static final String PROTOCOL_TAG = "protocol";

    //坑1:不要用@Autowired注入,拿不到对象,改用setter
    @Setter
    protected ServiceConfig serviceConfig;

    @Setter
    protected FilterConfig filterConfig = new FilterConfig();

    @Setter
    protected MeterRegistry meterRegistry;

    protected String getMetricsPrefixName() {
        return serviceConfig.getServiceName();
    }
}
