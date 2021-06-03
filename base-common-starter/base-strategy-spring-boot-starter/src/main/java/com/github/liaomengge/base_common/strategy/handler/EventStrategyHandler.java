package com.github.liaomengge.base_common.strategy.handler;

/**
 * Created by liaomengge on 2021/6/3.
 */
public interface EventStrategyHandler<I, O> {

    O doHandle(I i);
}
