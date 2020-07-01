package cn.ly.base_common.helper.delay;

import cn.ly.base_common.helper.delay.domain.DelayPojo;
import cn.ly.base_common.utils.date.LyDateUtil;
import cn.ly.base_common.utils.json.LyJsonUtil;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * Created by liaomengge on 18/1/9.
 */
public abstract class AbstractDelayDeqWorker extends AbstractDelayWorker implements MessageListener {

    private static final String[] PUB_REG_EXP = {"*", "?", "[", "]", "\\"};

    public AbstractDelayDeqWorker(String delayPrefix) {
        this.delayPrefix = delayPrefix;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        if (message != null) {
            String subscribeMessage = message.toString();
            if (StringUtils.isBlank(subscribeMessage) || this.containSpecialChar(subscribeMessage)) {
                logger.warn("发布的消息[" + subscribeMessage + "]不合法!");
                return;
            }
            logger.info("Delay发布的消息 ===> " + subscribeMessage);

            this.doHandle(subscribeMessage);
        }
    }

    public <T> void doHandle(String message) {
        if (!message.startsWith(BASE_DELAY_PREFIX + delayPrefix)) {
            return;
        }

        String jsonValue = jedisClusterHelper.get(message);
        if (StringUtils.isBlank(jsonValue)) {
            return;
        }

        DelayPojo<T> delayPojo = LyJsonUtil.fromJson(jsonValue, new TypeReference<DelayPojo<T>>() {
        });
        long delayExecTimeSecond = delayPojo.getDelayExecTimeSecond();
        long currentTimeSecond = LyDateUtil.getSecondTime();
        if (currentTimeSecond > delayExecTimeSecond) {
            logger.info("该uid[{}], 理论执行时间[{}], 实际执行时间[{}], 执行超时时间差[{}]s", message, delayExecTimeSecond, currentTimeSecond, (currentTimeSecond - delayExecTimeSecond));
        }

        this.process(delayPojo);
    }

    private boolean containSpecialChar(String str) {
        for (String regex : PUB_REG_EXP) {
            if (str.startsWith(regex) || str.endsWith(regex) || str.contains(regex)) {
                return true;
            }
        }
        return false;
    }

    protected abstract <T> void process(DelayPojo<T> delayPojo);
}
