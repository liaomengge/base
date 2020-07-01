package cn.ly.base_common.utils.log;

import cn.ly.base_common.utils.json.LyJsonUtil;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 记录指定格式的日志,非统一规范的格式
 * Created by liaomengge on 18/1/8.
 */
@UtilityClass
public class LyMetricLogUtil {

    private Logger logger = LoggerFactory.getLogger(LyMetricLogUtil.class);

    /**
     * 字符串填充形式log
     *
     * @param format
     * @param arguments
     */
    public void info(String format, Object... arguments) {
        logger.info(String.format(format, arguments));
    }

    /**
     * 对象直接log
     *
     * @param object
     */
    public void info(Object object) {
        logger.info(LyJsonUtil.toJson4Log(object));
    }
}
