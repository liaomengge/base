package cn.ly.base_common.xxljob;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Created by liaomengge on 2020/8/11.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "ly.xxl-job", name = "enabled")
@ConditionalOnClass(XxlJobSpringExecutor.class)
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobAutoConfiguration {

    @Value("${spring.application.name}")
    private String appName;

    @Autowired(required = false)
    private InetUtils inetUtils;

    private final XxlJobProperties xxlJobProperties;

    public XxlJobAutoConfiguration(XxlJobProperties xxlJobProperties) {
        this.xxlJobProperties = xxlJobProperties;
    }

    @Bean(initMethod = "start", destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public XxlJobExecutor xxlJobExecutor() {
        XxlJobProperties.AdminProperties adminProperties = this.xxlJobProperties.getAdmin();
        XxlJobProperties.ExecutorProperties executorProperties = this.xxlJobProperties.getExecutor();

        XxlJobExecutor xxlJobExecutor = new XxlJobSpringExecutor();
        xxlJobExecutor.setAppname(appName);
        xxlJobExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        xxlJobExecutor.setAdminAddresses(adminProperties.getAddresses());
        String ip = executorProperties.getIp();
        if (StringUtils.isBlank(ip) && Objects.nonNull(inetUtils)) {
            ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        }
        log.info("admin address => {}, ip => {}, port => {}", adminProperties.getAddresses(), ip,
                executorProperties.getPort());
        xxlJobExecutor.setIp(ip);
        xxlJobExecutor.setPort(executorProperties.getPort());
        xxlJobExecutor.setLogPath(executorProperties.getLogPath());
        xxlJobExecutor.setLogRetentionDays(executorProperties.getLogRetentionDays());
        return xxlJobExecutor;
    }
}
