package cn.ly.base_common.dayu.sentinel;

import cn.ly.base_common.dayu.domain.DayuBlockedDomain;
import cn.ly.base_common.dayu.sentinel.circuit.SentinelCircuitHandler;
import cn.ly.base_common.dayu.sentinel.datasource.SentinelDataSourceConfiguration;
import cn.ly.base_common.dayu.sentinel.filter.SentinelWebConfiguration;
import cn.ly.base_common.utils.web.MwWebUtil;
import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.adapter.servlet.config.WebServletConfig;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.util.AppNameUtil;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

import static cn.ly.base_common.dayu.sentinel.consts.SentinelConst.SENTINEL_PREFIX;

/**
 * Created by liaomengge on 2019/8/9.
 */
@Configuration
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
    @ConditionalOnClass(StatsDClient.class)
    public SentinelCircuitHandler sentinelCircuitHandler(StatsDClient statsDClient) {
        return new SentinelCircuitHandler(statsDClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    private void init() {
        if (StringUtils.hasText(projectName)) {
            System.setProperty(AppNameUtil.APP_NAME, projectName);
        }
        if (StringUtils.hasText(sentinelProperties.getServlet().getBlockPage())) {
            WebServletConfig.setBlockPage(sentinelProperties.getServlet().getBlockPage());
        }

        if (urlBlockHandler != null) {
            WebCallbackManager.setUrlBlockHandler(urlBlockHandler);
        } else {
            WebCallbackManager.setUrlBlockHandler((request, response, ex) -> MwWebUtil.renderJson(response,
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
