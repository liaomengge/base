package com.github.liaomengge.base_common.apollo.refresh;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.github.liaomengge.base_common.apollo.ApolloProperties;
import com.github.liaomengge.base_common.apollo.consts.ApolloConst;
import com.github.liaomengge.base_common.apollo.enums.RefreshTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Optional;
import java.util.Set;

/**
 * Created by liaomengge on 2020/8/1.
 */
@Slf4j
public class ApolloRefresher implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final RefreshScope refreshScope;
    private final ApolloProperties apolloProperties;

    public ApolloRefresher(RefreshScope refreshScope, ApolloProperties apolloProperties) {
        this.refreshScope = refreshScope;
        this.apolloProperties = apolloProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void refresh(ConfigChangeEvent changeEvent) {
        try {
            RefreshTypeEnum refreshTypeEnum = getRefreshTypeEnum(changeEvent);
            Set<String> changeKeys = this.apolloProperties.getRefreshScope().getChangeKeys();
            switch (refreshTypeEnum) {
                case SCOPE:
                    refreshScope(changeKeys);
                    return;
                case ALL:
                    refreshProperties(changeEvent);
                    refreshScope(changeKeys);
                    return;
                case PROPERTIES:
                default:
                    refreshProperties(changeEvent);
                    return;
            }
        } catch (Exception e) {
            log.error("apollo refresh fail", e);
        }
    }

    private RefreshTypeEnum getRefreshTypeEnum(ConfigChangeEvent changeEvent) {
        Set<String> changeKeys = changeEvent.changedKeys();
        if (CollectionUtils.containsAny(changeKeys, ApolloConst.REFRESH_TYPE_KEY)) {
            ConfigChange configChange = changeEvent.getChange(ApolloConst.REFRESH_TYPE_KEY);
            return Optional.ofNullable(configChange).map(ConfigChange::getNewValue)
                    .map(RefreshTypeEnum::getInstance).orElse(RefreshTypeEnum.PROPERTIES);
        }

        return RefreshTypeEnum.getInstance(applicationContext.getEnvironment().getProperty(ApolloConst.REFRESH_TYPE_KEY));
    }

    private void refreshProperties(ConfigChangeEvent changeEvent) {
        try {
            applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        } catch (Exception e) {
            log.warn("refresh properties fail", e);
        }
    }

    private void refreshScope(Set<String> changeKeys) {
        try {
            if (CollectionUtils.isEmpty(changeKeys)) {
                refreshScope.refreshAll();
                return;
            }
            changeKeys.stream().forEach(refreshScope::refresh);
        } catch (Exception e) {
            log.warn("refresh scope fail", e);
        }
    }
}
