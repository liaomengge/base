package com.github.liaomengge.base_common.strategy.annotation;

/**
 * Created by liaomengge on 2021/6/3.
 */
public interface IStrategy {

    default String getValue() {
        return this.getClass().getTypeName();
    }

    default String getCategory() {
        return "";
    }
}
