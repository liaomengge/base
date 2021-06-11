package com.github.liaomengge.base_common.design.patterns.strategy.handler;

/**
 * Created by liaomengge on 2021/6/3.
 */
public interface StrategyHandler<I, O> {

    default O doHandle(I... i) {
        return null;
    }
}
