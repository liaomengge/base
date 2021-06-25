package com.github.liaomengge.base_common.utils.log;

import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 记录指定格式的日志,非统一规范的格式
 * Created by liaomengge on 18/1/8.
 */
@Slf4j
@UtilityClass
public class LyMetricLogUtil {
    
    /**
     * 字符串填充形式log
     *
     * @param format
     * @param arguments
     */
    public void info(String format, Object... arguments) {
        log.info(String.format(format, arguments));
    }

    /**
     * 对象直接log
     *
     * @param object
     */
    public void info(Object object) {
        log.info(LyJsonUtil.toJson4Log(object));
    }

    /**
     * 字符串填充形式log
     *
     * @param format
     * @param arguments
     */
    public void warn(String format, Object... arguments) {
        log.warn(String.format(format, arguments));
    }

    /**
     * 对象直接log
     *
     * @param object
     */
    public void warn(Object object) {
        log.warn(LyJsonUtil.toJson4Log(object));
    }

    /**
     * 字符串填充形式log
     *
     * @param format
     * @param arguments
     */
    public void error(String format, Object... arguments) {
        log.error(String.format(format, arguments));
    }

    /**
     * 对象直接log
     *
     * @param object
     */
    public void error(Object object) {
        log.error(LyJsonUtil.toJson4Log(object));
    }
}
