package com.github.liaomengge.base_common.utils.log;

import com.github.liaomengge.base_common.utils.json.LyJsonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.UtilityClass;

/**
 * 记录指定格式的日志,非统一规范的格式
 * Created by liaomengge on 18/1/8.
 */
@UtilityClass
public class LyMetricLogUtil {

    private Logger log = LoggerFactory.getLogger(LyMetricLogUtil.class);

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
}
