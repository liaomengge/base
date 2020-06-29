package cn.mwee.base_common.utils.log;

import cn.mwee.base_common.utils.json.MwJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 记录指定格式的日志,非统一规范的格式
 * Created by liaomengge on 18/1/8.
 */
public final class MwMetricLogUtil {

    private static Logger logger = LoggerFactory.getLogger(MwMetricLogUtil.class);

    private MwMetricLogUtil() {
    }

    /**
     * 字符串填充形式log
     *
     * @param format
     * @param arguments
     */
    public static void info(String format, Object... arguments) {
        logger.info(String.format(format, arguments));
    }

    /**
     * 对象直接log
     *
     * @param object
     */
    public static void info(Object object) {
        logger.info(MwJsonUtil.toJson4Log(object));
    }
}
