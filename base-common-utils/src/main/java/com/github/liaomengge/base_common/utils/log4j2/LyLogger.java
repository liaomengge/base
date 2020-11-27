package com.github.liaomengge.base_common.utils.log4j2;

import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.text.LyEscapeJsonUtil;
import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.IllegalFormatException;


/**
 * Created by liaomengge on 17/11/17.
 */
public class LyLogger implements Logger {

    private final static String WRAPPER = "";

    private final Logger log;

    private Class<?> clz;

    private String name;

    public LyLogger(Class clz) {
        this.clz = clz;
        log = LoggerFactory.getLogger(this.clz);
    }

    public LyLogger(String name) {
        this.name = name;
        log = LoggerFactory.getLogger(this.name);
    }

    public static LyLogger getInstance(Class<?> clz) {
        return new LyLogger(clz);
    }

    public static LyLogger getInstance(String name) {
        return new LyLogger(name);
    }

    @Override
    public String getName() {
        return clz.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String format) {
        log.trace(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void trace(String format, Object arg) {
        log.trace(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        log.trace(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void trace(String format, Object... arguments) {
        log.trace(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void trace(String format, Throwable t) {
        log.trace(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return true;
    }

    @Override
    public void trace(Marker marker, String format) {
        log.trace(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        log.trace(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        log.trace(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void trace(Marker marker, String format, Object... arguments) {
        log.trace(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void trace(Marker marker, String format, Throwable t) {
        log.trace(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String format) {
        log.debug(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void debug(String format, Object arg) {
        log.debug(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        log.debug(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void debug(String format, Object... arguments) {
        log.debug(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void debug(String format, Throwable t) {
        log.debug(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return true;
    }

    public void debug(LyLogData logData) {
        log.debug(formatLogData(logData));
    }

    @Override
    public void debug(Marker marker, String format) {
        log.debug(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        log.debug(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        log.debug(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        log.debug(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void debug(Marker marker, String format, Throwable t) {
        log.debug(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String format) {
        log.info(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void info(String format, Object arg) {
        log.info(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        log.info(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void info(String format, Object... arguments) {
        log.info(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void info(String format, Throwable t) {
        log.info(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    public void info(LyLogData logData) {
        log.info(formatLogData(logData));
    }

    @Override
    public void info(Marker marker, String format) {
        log.info(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        log.info(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        log.info(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        log.info(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void info(Marker marker, String format, Throwable t) {
        log.info(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String format) {
        log.warn(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void warn(String format, Object arg) {
        log.warn(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void warn(String format, Object... arguments) {
        log.warn(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        log.warn(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void warn(String format, Throwable t) {
        log.warn(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return true;
    }

    public void warn(LyLogData logData) {
        log.warn(formatLogData(logData));
    }

    @Override
    public void warn(Marker marker, String format) {
        log.warn(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        log.warn(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        log.warn(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        log.warn(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void warn(Marker marker, String format, Throwable t) {
        log.warn(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String format) {
        log.error(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void error(String format, Object arg) {
        log.error(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log.error(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void error(String format, Object... arguments) {
        log.error(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void error(String format, Throwable t) {
        log.error(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return true;
    }

    public void error(LyLogData logData) {
        log.error(formatLogData(logData));
    }

    @Override
    public void error(Marker marker, String format) {
        log.error(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        log.error(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        log.error(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        log.error(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void error(Marker marker, String format, Throwable t) {
        log.error(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    public String formatLogData(LyLogData data) {
        if (data == null) {
            return "";
        }

        data.setTraceId(StringUtils.defaultString(LyTraceLogUtil.get(), "---"));
        return LyEscapeJsonUtil.escapeJson(LyJsonUtil.toJson4Log(data));
    }

    private String addTraceAndEscapeJson(String src) {
        if (StringUtils.isBlank(src)) {
            return "";
        }

        String traceId = LyTraceLogUtil.get();
        if (StringUtils.isBlank(traceId)) {
            return LyEscapeJsonUtil.escapeJson(src);
        }
        return "TraceID[" + traceId + "], " + LyEscapeJsonUtil.escapeJson(src);
    }

    private String replaceFormat(String format, Object... values) {
        format = StringUtils.replace(format, "{}", "%s");
        try {
            format = String.format(format, values);
        } catch (IllegalFormatException e) {
        }
        return addTraceAndEscapeJson(format);
    }
}
