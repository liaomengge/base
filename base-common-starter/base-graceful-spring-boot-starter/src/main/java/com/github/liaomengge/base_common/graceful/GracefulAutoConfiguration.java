package com.github.liaomengge.base_common.graceful;

import com.github.liaomengge.base_common.graceful.tomcat.TomcatAutoConfiguration;
import com.github.liaomengge.base_common.graceful.undertow.UndertowAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2018/12/18.
 */
@Configuration
@Import({TomcatAutoConfiguration.class, UndertowAutoConfiguration.class})
@EnableConfigurationProperties(GracefulProperties.class)
public class GracefulAutoConfiguration {
}
