package com.github.liaomengge.base_common.xxljob;

import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Created by liaomengge on 2020/8/11.
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "base.xxl-job", name = "enabled")
@ConditionalOnClass(XxlJobSpringExecutor.class)
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobAutoConfiguration {

    @Value("${spring.application.name}")
    private String appName;

    private final XxlJobProperties xxlJobProperties;

    public XxlJobAutoConfiguration(XxlJobProperties xxlJobProperties) {
        this.xxlJobProperties = xxlJobProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public XxlJobSpringExecutor xxlJobSpringExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();

        XxlJobProperties.AdminProperties adminProperties = this.xxlJobProperties.getAdmin();
        XxlJobProperties.ExecutorProperties executorProperties = this.xxlJobProperties.getExecutor();

        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        xxlJobSpringExecutor.setAdminAddresses(adminProperties.getAddresses());
        String ip = executorProperties.getIp();
        if (StringUtils.isBlank(ip)) {
            ip = LyNetworkUtil.getIpAddress();
        }
        log.info("admin address => {}, ip => {}, port => {}", adminProperties.getAddresses(), ip,
                executorProperties.getPort());
        xxlJobSpringExecutor.setIp(ip);
        if (Objects.nonNull(executorProperties.getPort())) {
            xxlJobSpringExecutor.setPort(executorProperties.getPort());
        }
        if (StringUtils.isNoneBlank(executorProperties.getLogPath())) {
            xxlJobSpringExecutor.setLogPath(executorProperties.getLogPath());
        }
        if (Objects.nonNull(executorProperties.getLogRetentionDays())) {
            xxlJobSpringExecutor.setLogRetentionDays(executorProperties.getLogRetentionDays());
        }
        return xxlJobSpringExecutor;
    }
}
