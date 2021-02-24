package com.github.liaomengge.base_common.graceful.undertow;

import com.github.liaomengge.base_common.graceful.undertow.handler.GracefulShutdownWrapper;
import io.undertow.UndertowOptions;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * Created by liaomengge on 2021/2/24.
 */
@AllArgsConstructor
public class UndertowShutdownWebServerFactoryCustomizer implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    private final GracefulShutdownWrapper gracefulShutdownWrapper;

    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(deploymentInfo -> deploymentInfo.addOuterHandlerChainWrapper(gracefulShutdownWrapper));
        factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_STATISTICS, true));
    }
}
