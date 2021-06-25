package com.github.liaomengge.base_common.quartz.domain;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 19/1/30.
 */
public abstract class AbstractBaseJob {

    protected final Logger log = LoggerFactory.getLogger(AbstractBaseJob.class);

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

        long startTime = System.nanoTime();

        try {
            work();
        } catch (Exception e) {
            log.error("执行[" + getClass().getSimpleName() + "]异常", e);
        }

        long endTime = System.nanoTime();
        log.info("执行完[{}]耗费: {}ms", getClass().getSimpleName(), TimeUnit.NANOSECONDS.toMillis(endTime - startTime));

        if (nextJob != null) {
            nextJob.execute();
        }
    }
}
