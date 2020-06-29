package cn.mwee.base_common.helper.concurrent.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.Callable;

/**
 * Created by liaomengge on 2018/12/14.
 */
@AllArgsConstructor
public abstract class TraceIdCallable implements Callable {

    private String threadTraceId;
}
