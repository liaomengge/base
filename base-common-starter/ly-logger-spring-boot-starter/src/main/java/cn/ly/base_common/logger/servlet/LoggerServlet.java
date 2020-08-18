package cn.ly.base_common.log.servlet;

import cn.ly.base_common.log.LoggerProperties;
import cn.ly.base_common.utils.web.LyWebUtil;
import lombok.Data;
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
        Boolean enabled = this.environment.getProperty("endpoints.loggers.enabled", Boolean.class);
        if (!Boolean.TRUE.equals(enabled)) {
            respBody.setSuccess(false);
            respBody.setMsg("endpoints.loggers.enabled必须开启");
            LyWebUtil.renderJson(resp, respBody);
            return;
        }

        LogLevel logLevel;
        try {
            logLevel = this.getLogLevel(loggerProperties.getLevel());
        } catch (Exception e) {
            respBody.setSuccess(false);
            respBody.setMsg(e.getMessage());
            LyWebUtil.renderJson(resp, respBody);
            return;
        }

        try {
            loggersEndpoint.configureLogLevel(loggerProperties.getPkg(), LogLevel.valueOf(logLevel.name()));
            respBody.setMsg("设置package[" + loggerProperties.getPkg() + "],级别[" + loggerProperties.getLevel() + "]成功");
        } catch (Exception e) {
            respBody.setSuccess(false);
            respBody.setMsg("设置package[" + loggerProperties.getPkg() + "],级别[" + loggerProperties.getLevel() + "]失败");
        }
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
