package com.github.liaomengge.base_common.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import com.github.liaomengge.base_common.apollo.listener.ApolloRefreshListener;
import com.github.liaomengge.base_common.apollo.refresh.ApolloRefresher;
import com.github.liaomengge.base_common.apollo.refresh.conditional.ApolloConditionalRefresh;
import com.github.liaomengge.base_common.apollo.refresh.conditional.manager.ConditionalOnPropertyManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(ApolloProperties.class)
public class ApolloAutoConfiguration {

    @Value("${apollo.bootstrap.namespaces:application}")
    private String namespaces;

    private final RefreshScope refreshScope;

    public ApolloAutoConfiguration(RefreshScope refreshScope) {
        this.refreshScope = refreshScope;
    }

    @Bean
    @ConditionalOnMissingBean
    public ApolloRefresher apolloRefresher(ApolloProperties apolloProperties) {
        return new ApolloRefresher(refreshScope, apolloProperties);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = "base.apollo.conditional.enabled", havingValue = "true")
    public class ConditionalConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ConditionalOnPropertyManager conditionalOnPropertyManager(ApolloProperties apolloProperties) {
            return new ConditionalOnPropertyManager(apolloProperties);
        }

        @Bean
        @ConditionalOnMissingBean
        public ApolloConditionalRefresh apolloConditionalRefresh(ConditionalOnPropertyManager conditionalOnPropertyManager) {
            return new ApolloConditionalRefresh(conditionalOnPropertyManager);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ApolloRefreshListener apolloAutoRefreshListener(ApolloRefresher apolloRefresher,
                                                           ObjectProvider<ApolloConditionalRefresh> objectProvider) {
        ApolloRefreshListener apolloAutoRefreshListener = new ApolloRefreshListener(apolloRefresher,
                objectProvider.getIfAvailable());
        SPLITTER.split(this.namespaces).forEach(namespace -> {
            Config config = ConfigService.getConfig(namespace);
            config.addChangeListener(apolloAutoRefreshListener);
        });
        return apolloAutoRefreshListener;
    }
}
