package com.github.liaomengge.base_common.utils.retry;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by liaomengge on 16/10/31.
 */
@Slf4j
@UtilityClass
public class LyIdempotentUtil {

    private final int RETRY_NUM = 3;

    public void process(IdempotentHelper helper) {
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
                log.info("重试次数: {}", count);
                helper.doException(e);
                continue;
            }
        }

        if (reTry < 0) {
            log.error("重试{}次,仍执行失败", RETRY_NUM);
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
