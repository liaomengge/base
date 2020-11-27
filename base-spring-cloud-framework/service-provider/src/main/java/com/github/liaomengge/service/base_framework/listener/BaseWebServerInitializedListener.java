package com.github.liaomengge.service.base_framework.listener;

import com.github.liaomengge.service.base_framework.util.PrintLayoutUtil;
import com.taobao.text.ui.TableElement;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.JOINER;

/**
 * Created by liaomengge on 2019/11/29.
 */
@Component
public class BaseWebServerInitializedListener {

    private static final String HTTP_PREFIX = "http://";

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @EventListener(WebServerInitializedEvent.class)
    public void afterWebInitialize(WebServerInitializedEvent event) {
        WebServerApplicationContext context = event.getApplicationContext();
        if (isDevOrTestEnv(context)) {
            ServerProperties serverProperties = context.getBean(ServerProperties.class);
            WebEndpointProperties webEndpointProperties = context.getBean(WebEndpointProperties.class);
            String serverContextPath = StringUtils.defaultIfBlank(serverProperties.getServlet().getContextPath(), "");
            String endpointBasePath = StringUtils.defaultIfBlank(webEndpointProperties.getBasePath(), "");
            String applicationName = context.getEnvironment().getProperty("spring.application.name");
            String[] activeProfiles = context.getEnvironment().getActiveProfiles();
            if (ArrayUtils.isEmpty(activeProfiles)) {
                activeProfiles = context.getEnvironment().getDefaultProfiles();
            }

            StringBuilder sBuilder = new StringBuilder(16);
            sBuilder.append("\n");
            sBuilder.append("---------------------------------------------------------------------------").append("\n");
            //management.info.build.enabled=true
            //management.endpoint.info.enabled=true
            String infoUrl = HTTP_PREFIX + getIpAndPort(event) + serverContextPath + endpointBasePath + "/info";
            TableElement tableElement = PrintLayoutUtil.buildTableStyle();
            PrintLayoutUtil.addRowElement(tableElement, "APPLICATION NAME: ", applicationName);
            PrintLayoutUtil.addRowElement(tableElement, "ACTIVE PROFILES: ", JOINER.join(activeProfiles));
            PrintLayoutUtil.addRowElement(tableElement, "INFO URL: ", infoUrl);
            if (ClassUtils.isPresent("springfox.documentation.spring.web.plugins.Docket", null)) {
                String swaggerUrl = HTTP_PREFIX + getIpAndPort(event) + serverContextPath + "/doc.html";
                PrintLayoutUtil.addRowElement(tableElement, "SWAGGER URL: ", swaggerUrl);
            }
            sBuilder.append(PrintLayoutUtil.render(tableElement));
            sBuilder.append("---------------------------------------------------------------------------").append("\n");
            System.out.println(sBuilder.toString());
        }
    }

    private String getIpAndPort(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        String hostAddress = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostAddress = address.getHostAddress();
        } catch (UnknownHostException e) {
        }
        hostAddress = StringUtils.defaultIfBlank(hostAddress, "localhost");
        return hostAddress + ":" + port;
    }

    private boolean isDevOrTestEnv(WebServerApplicationContext ctx) {
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        if (ArrayUtils.isNotEmpty(activeProfiles)) {
            return Arrays.stream(activeProfiles).anyMatch(val -> StringUtils.equalsIgnoreCase(val, "dev") || StringUtils.equalsIgnoreCase(val, "test") || StringUtils.equalsIgnoreCase(val, "fat"));
        }
        return true;
    }
}
