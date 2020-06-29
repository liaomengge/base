package cn.mwee.base_common.graceful;

import cn.mwee.base_common.graceful.tomcat.TomcatShutdown;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2018/12/18.
 */
@Configuration
@ConditionalOnProperty(prefix = "mwee.graceful.tomcat", name = "enabled")
@EnableConfigurationProperties(GracefulProperties.class)
@ConditionalOnClass({ApplicationListener.class, Tomcat.class})
public class GracefulAutoConfiguration {

    @Autowired
    private GracefulProperties gracefulProperties;

    //@Bean
    //public ContextShutdown contextShutdown() {
    //    return new ContextShutdown(gracefulProperties);
    //}

    @Bean
    public TomcatShutdown tomcatShutdown() {
        return new TomcatShutdown(gracefulProperties);
    }

    @Bean
    public EmbeddedServletContainerCustomizer tomcatCustomizer(TomcatShutdown tomcatShutdown) {
        return container -> {
            if (container instanceof TomcatEmbeddedServletContainerFactory) {
                ((TomcatEmbeddedServletContainerFactory) container).addConnectorCustomizers(tomcatShutdown);
            }
        };
    }
}
