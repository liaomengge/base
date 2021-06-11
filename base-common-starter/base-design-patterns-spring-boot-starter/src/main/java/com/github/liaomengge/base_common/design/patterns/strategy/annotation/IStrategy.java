package com.github.liaomengge.base_common.design.patterns.strategy.annotation;

/**
 * Created by liaomengge on 2021/6/3.
 */
public interface IStrategy {

    default String value() {
        return this.getClass().getTypeName();
    }

    default String category() {
        return "";
    }

    default IStrategy strategy() {
        return null;
    }
}
