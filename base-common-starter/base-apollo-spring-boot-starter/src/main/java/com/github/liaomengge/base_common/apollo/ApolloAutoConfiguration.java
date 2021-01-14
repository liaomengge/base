package com.github.liaomengge.base_common.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import com.github.liaomengge.base_common.apollo.listener.ApolloRefreshListener;
import com.github.liaomengge.base_common.apollo.refresh.ApolloRefresher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2020/8/1.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED)
@ConditionalOnClass({ConfigChangeListener.class, ConfigChangeEvent.class, RefreshScope.class})
public class ApolloAutoConfiguration {

    @Value("${apollo.bootstrap.namespaces:application}")
    private String namespaces;

    private final RefreshScope refreshScope;

    public ApolloAutoConfiguration(RefreshScope refreshScope) {
        this.refreshScope = refreshScope;
    }

    @Bean
    @ConditionalOnMissingBean
    public ApolloRefresher apolloRefresher() {
        return new ApolloRefresher(refreshScope);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApolloRefreshListener apolloAutoRefreshListener(ApolloRefresher apolloRefresher) {
        ApolloRefreshListener apolloAutoRefreshListener = new ApolloRefreshListener(apolloRefresher);
        SPLITTER.split(this.namespaces).forEach(namespace -> {
            Config config = ConfigService.getConfig(namespace);
            config.addChangeListener(apolloAutoRefreshListener);
        });
        return apolloAutoRefreshListener;
    }
}
