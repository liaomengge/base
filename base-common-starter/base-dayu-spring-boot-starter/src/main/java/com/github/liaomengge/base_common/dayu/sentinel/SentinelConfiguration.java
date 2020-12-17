package com.github.liaomengge.base_common.dayu.sentinel;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.adapter.servlet.config.WebServletConfig;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.util.AppNameUtil;
import com.github.liaomengge.base_common.dayu.domain.DayuBlockedDomain;
import com.github.liaomengge.base_common.dayu.sentinel.circuit.SentinelCircuitHandler;
import com.github.liaomengge.base_common.dayu.sentinel.datasource.SentinelDataSourceConfiguration;
import com.github.liaomengge.base_common.dayu.sentinel.filter.SentinelWebConfiguration;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

import static com.github.liaomengge.base_common.dayu.sentinel.consts.SentinelConst.SENTINEL_PREFIX;

/**
 * Created by liaomengge on 2019/8/9.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = SENTINEL_PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SentinelProperties.class)
@Import({SentinelWebConfiguration.class, SentinelDataSourceConfiguration.class})
public class SentinelConfiguration {

    @Value("${spring.application.name}")
    private String projectName;

    @Autowired(required = false)
    private UrlCleaner urlCleaner;

    @Autowired(required = false)
    private UrlBlockHandler urlBlockHandler;

    @Autowired(required = false)
    private RequestOriginParser requestOriginParser;

    @Autowired
    private SentinelProperties sentinelProperties;

    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    public SentinelCircuitHandler sentinelCircuitHandler(MeterRegistry meterRegistry) {
        return new SentinelCircuitHandler(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    private void init() {
        if (StringUtils.isNoneBlank(projectName)) {
            System.setProperty(AppNameUtil.getAppName(), projectName);
        }
        if (StringUtils.isNoneBlank(sentinelProperties.getServlet().getBlockPage())) {
            WebServletConfig.setBlockPage(sentinelProperties.getServlet().getBlockPage());
        }

        if (urlBlockHandler != null) {
            WebCallbackManager.setUrlBlockHandler(urlBlockHandler);
        } else {
            WebCallbackManager.setUrlBlockHandler((request, response, ex) -> LyWebUtil.renderJson(response,
                    DayuBlockedDomain.create()));
        }
        if (urlCleaner != null) {
            WebCallbackManager.setUrlCleaner(urlCleaner);
        }
        if (requestOriginParser != null) {
            WebCallbackManager.setRequestOriginParser(requestOriginParser);
        }
    }
}
