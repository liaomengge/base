package cn.ly.base_common.helper.delay;

import cn.ly.base_common.utils.json.MwJsonUtil;
import cn.ly.base_common.helper.delay.domain.DelayPojo;
import cn.ly.base_common.helper.redis.RedisTemplateHelper;
import lombok.Setter;

/**
 * Created by liaomengge on 18/1/9.
 */
public class DelayEnqWorker extends AbstractDelayWorker {

    private static final int UNIT_TIME_SECOND = 60;

    @Setter
    private RedisTemplateHelper redisTemplateHelper;

    public DelayEnqWorker(String delayPrefix) {
        this.delayPrefix = delayPrefix;
    }

    public <T> void doHandle(DelayPojo<T> delayPojo) {
        final String key = BASE_DELAY_PREFIX + this.delayPrefix + ":" + delayPojo.getUid();
        int expiredSeconds = delayPojo.getDelayTimeSecond();
        jedisClusterHelper.set(key, MwJsonUtil.toJson(delayPojo), expiredSeconds + 1 * UNIT_TIME_SECOND * UNIT_TIME_SECOND);

        redisTemplateHelper.set(key, "", expiredSeconds);
    }
}
