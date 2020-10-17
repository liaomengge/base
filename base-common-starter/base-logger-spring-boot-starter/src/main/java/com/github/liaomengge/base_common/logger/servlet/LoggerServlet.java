package com.github.liaomengge.base_common.logger.servlet;

import com.github.liaomengge.base_common.logger.LoggerProperties;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * Created by liaomengge on 2019/1/22.
 */
public class LoggerServlet extends HttpServlet implements EnvironmentAware {

    private static final long serialVersionUID = -4795305296014118807L;

    private Environment environment;

    @Autowired
    private LoggerProperties loggerProperties;

    @Autowired
    private LoggersEndpoint loggersEndpoint;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doHandle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    private void doHandle(HttpServletRequest req, HttpServletResponse resp) {
        RespBody respBody = new RespBody();
        Boolean enabled = this.environment.getProperty("management.endpoint.loggers.enabled", Boolean.class);
        if (!Boolean.TRUE.equals(enabled)) {
            respBody.setSuccess(false);
            respBody.setMsg("management.endpoint.loggers.enabled必须开启");
            LyWebUtil.renderJson(resp, respBody);
            return;
        }

        Map<String, String> configureLevel = loggerProperties.getConfigureLevel();
        if (MapUtils.isNotEmpty(configureLevel)) {
            configureLevel.forEach((key, val) -> loggersEndpoint.configureLogLevel(key,
                    LogLevel.valueOf(StringUtils.upperCase(val))));
            respBody.setMsg("设置configureLevel[" + LyJacksonUtil.bean2Json(configureLevel) + "]成功");
            LyWebUtil.renderJson(resp, respBody);
            return;
        }
        respBody.setSuccess(false);
        respBody.setMsg("configureLevel为空,设置失败");
        LyWebUtil.renderJson(resp, respBody);
    }

    private LogLevel getLogLevel(String level) {
        return (level != null) ? LogLevel.valueOf(level.toUpperCase(Locale.ENGLISH)) : null;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Data
    private static class RespBody {
        private boolean success;
        private String msg;

        public RespBody() {
            this.success = true;
        }
    }
}
