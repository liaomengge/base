package cn.mwee.base_common.helper.concurrent.thread;

import lombok.AllArgsConstructor;

/**
 * Created by liaomengge on 2018/12/14.
 */
@AllArgsConstructor
public abstract class TraceIdRunnable implements Runnable {

    private String threadTraceId;
}
