package cn.ly.base_common.quartz.domain;

import cn.ly.base_common.utils.log4j2.LyLogger;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 19/1/30.
 */
public abstract class AbstractBaseJob {

    protected final Logger log = LyLogger.getInstance(AbstractBaseJob.class);

    @Getter
    @Setter
    protected AbstractBaseJob nextJob;

    protected void init() {
        //初始化动作
        log.info("[" + getClass().getSimpleName() + "]开始执行...");
    }

    public abstract void work();

    public void execute() {
        init();

        long startTime = System.currentTimeMillis();

        try {
            work();
        } catch (Exception e) {
            log.error("执行[" + getClass().getSimpleName() + "]异常", e);
        }

        long endTime = System.currentTimeMillis();
        log.info("执行完[{}]耗费: {}ms", getClass().getSimpleName(), (endTime - startTime));

        if (nextJob != null) {
            nextJob.execute();
        }
    }
}
