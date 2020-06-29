package cn.mwee.base_common.utils.retry;

import cn.mwee.base_common.utils.log4j2.MwLogger;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 16/10/31.
 */
public final class MwIdempotentUtil {

    private final static Logger logger = MwLogger.getInstance(MwIdempotentUtil.class);

    private static final int RETRY_NUM = 3;

    private MwIdempotentUtil() {
    }

    public static void process(IdempotentHelper helper) {
        int reTry = RETRY_NUM;
        int count = 0;
        while (reTry-- > 0) {
            try {
                boolean isOk = helper.handle();
                if (isOk) {
                    return;
                }
            } catch (Exception e) {
                count++;
                logger.info("重试次数: {}", count);
                helper.doException(e);
                continue;
            }
        }

        if (reTry < 0) {
            logger.error("重试" + RETRY_NUM + "次,仍执行失败");
            helper.doRetryOver();
        }
    }

    public abstract class IdempotentHelper {

        public abstract boolean handle();

        public void doException(Exception e) {
        }

        public void doRetryOver() {
        }
    }
}
