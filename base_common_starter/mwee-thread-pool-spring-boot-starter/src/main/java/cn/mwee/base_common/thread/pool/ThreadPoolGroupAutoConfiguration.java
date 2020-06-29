package cn.mwee.base_common.thread.pool;

import cn.mwee.base_common.thread.pool.registry.ThreadPoolBeanRegistryConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/5/17.
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolGroupProperties.class)
@Import(ThreadPoolBeanRegistryConfiguration.class)
public class ThreadPoolGroupAutoConfiguration {
}
