package com.github.liaomengge.base_common.graceful.tomcat;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * Created by liaomengge on 2021/2/24.
 */
@AllArgsConstructor
public class TomcatShutdownWebServerFactoryCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private final TomcatConnectorCustomizer tomcatConnectorCustomizer;

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(tomcatConnectorCustomizer);
    }
}
