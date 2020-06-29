package cn.mwee.base_common.helper.delay.domain;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 18/1/9.
 */
@Data
public class DelayPojo<T> {

    private String uid;//唯一标识延时消息
    private long delayStartTimeSecond;//开始延时时间,单位:秒
    private int delayTime;//延时时间
    private TimeUnit delayTimeUnit;//延时单位
    private T body;

    public int getDelayTimeSecond() {
        return (int) this.delayTimeUnit.toSeconds(this.delayTime);
    }

    public long getDelayExecTimeSecond() {
        return this.delayStartTimeSecond + this.getDelayTimeSecond();
    }
}
