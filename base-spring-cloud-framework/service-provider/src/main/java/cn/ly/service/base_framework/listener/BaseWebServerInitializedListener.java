package cn.ly.service.base_framework.listener;

import cn.ly.base_common.support.misc.consts.ToolConst;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by liaomengge on 2019/11/29.
 */
@Component
public class BaseWebServerInitializedListener {

    private static final String HTTP_PREFIX = "http://";

    @Order
    @EventListener(EmbeddedServletContainerInitializedEvent.class)
    public void afterWebInitialize(EmbeddedServletContainerInitializedEvent event) {
        ConfigurableApplicationContext ctx = event.getApplicationContext();
        if (isDevOrTestEnv(ctx)) {
            ServerProperties serverProperties = ctx.getBean(ServerProperties.class);
            ManagementServerProperties managementServerProperties = ctx.getBean(ManagementServerProperties.class);
            String serverContextPath = serverProperties.getContextPath();
            String managementContextPath = managementServerProperties.getContextPath();
            String applicationName = ctx.getEnvironment().getProperty("spring.application.name");
            String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();

            String pingUrl = HTTP_PREFIX + getIpAndPort(event) + serverContextPath + managementContextPath + "/info";
            StringBuilder sBuilder = new StringBuilder(16);
            sBuilder.append("\n");
            sBuilder.append("---------------------------------------------------------------------------").append("\n");
            sBuilder.append("APPLICATION NAME: ").append(applicationName).append("\n");
            sBuilder.append("ACTIVE PROFILES:  ").append(ToolConst.JOINER.join(activeProfiles)).append("\n");
            sBuilder.append("PING URL:         ").append(pingUrl).append("\n");
            if (ClassUtils.isPresent("springfox.documentation.spring.web.plugins.Docket", null)) {
                String swaggerUrl = HTTP_PREFIX + getIpAndPort(event) + serverContextPath + "/doc.html";
                sBuilder.append("SWAGGER URL:      ").append(swaggerUrl).append("\n");
            }
            sBuilder.append("---------------------------------------------------------------------------").append("\n");
            System.err.println(sBuilder.toString());
        }
    }

    private String getIpAndPort(EmbeddedServletContainerInitializedEvent event) {
        int port = event.getEmbeddedServletContainer().getPort();
        String hostAddress = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostAddress = address.getHostAddress();
        } catch (UnknownHostException e) {
        }
        hostAddress = StringUtils.defaultIfBlank(hostAddress, "localhost");
        return hostAddress + ":" + port;
    }

    private boolean isDevOrTestEnv(ConfigurableApplicationContext ctx) {
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        if (ArrayUtils.isNotEmpty(activeProfiles)) {
            return Arrays.stream(activeProfiles).anyMatch(val -> StringUtils.equalsIgnoreCase(val, "dev") || StringUtils.equalsIgnoreCase(val, "test"));
        }
        return true;
    }
}
