package com.github.liaomengge.base_common.support.logger;

import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.text.LyEscapeJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.IllegalFormatException;


/**
 * Created by liaomengge on 17/11/17.
 * 针对请求参数，打印出json参数，而不是toString参数
 */
public class JsonLogger {

    private final static String WRAPPER = "";

    private final Logger log;

    private Class<?> clazz;

    private String name;

    public JsonLogger(Class clazz) {
        this.clazz = clazz;
        log = LoggerFactory.getLogger(this.clazz);
    }

    public JsonLogger(String name) {
        this.name = name;
        log = LoggerFactory.getLogger(this.name);
    }

    public static JsonLogger getInstance(Class<?> clazz) {
        return new JsonLogger(clazz);
    }

    public static JsonLogger getInstance(String name) {
        return new JsonLogger(name);
    }

    public void info(String format) {
        log.info(WRAPPER + escapeJson(format) + WRAPPER);
    }

    public void info(String format, Object arg) {
        log.info(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    public void info(String format, Object arg1, Object arg2) {
        log.info(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    public void info(String format, Object... arguments) {
        log.info(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    public void info(String format, Throwable t) {
        log.info(WRAPPER + escapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    public void info(Marker marker, String format) {
        log.info(marker, WRAPPER + escapeJson(format) + WRAPPER);
    }

    public void info(Marker marker, String format, Object arg) {
        log.info(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    public void info(Marker marker, String format, Object arg1, Object arg2) {
        log.info(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    public void info(Marker marker, String format, Object... arguments) {
        log.info(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    public void info(Marker marker, String format, Throwable t) {
        log.info(marker, WRAPPER + escapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    public void warn(String format) {
        log.warn(WRAPPER + escapeJson(format) + WRAPPER);
    }

    public void warn(String format, Object arg) {
        log.warn(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    public void warn(String format, Object... arguments) {
        log.warn(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    public void warn(String format, Object arg1, Object arg2) {
        log.warn(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    public void warn(String format, Throwable t) {
        log.warn(WRAPPER + escapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    public void warn(Marker marker, String format) {
        log.warn(marker, WRAPPER + escapeJson(format) + WRAPPER);
    }

    public void warn(Marker marker, String format, Object arg) {
        log.warn(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        log.warn(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    public void warn(Marker marker, String format, Object... arguments) {
        log.warn(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    public void warn(Marker marker, String format, Throwable t) {
        log.warn(marker, WRAPPER + escapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }


    public void error(String format) {
        log.error(WRAPPER + escapeJson(format) + WRAPPER);
    }

    public void error(String format, Object arg) {
        log.error(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    public void error(String format, Object arg1, Object arg2) {
        log.error(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    public void error(String format, Object... arguments) {
        log.error(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    public void error(String format, Throwable t) {
        log.error(WRAPPER + escapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    public void error(Marker marker, String format) {
        log.error(marker, WRAPPER + escapeJson(format) + WRAPPER);
    }

    public void error(Marker marker, String format, Object arg) {
        log.error(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    public void error(Marker marker, String format, Object arg1, Object arg2) {
        log.error(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    public void error(Marker marker, String format, Object... arguments) {
        log.error(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    public void error(Marker marker, String format, Throwable t) {
        log.error(marker, WRAPPER + escapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    private String replaceFormat(String format, Object... values) {
        format = StringUtils.replace(format, "{}", "%s");
        try {
            format = String.format(format, values);
        } catch (IllegalFormatException e) {
        }
        return escapeJson(format);
    }

    private String escapeJson(String src) {
        if (StringUtils.isBlank(src)) {
            return "";
        }

        return LyEscapeJsonUtil.escapeJson(src);
    }
}
