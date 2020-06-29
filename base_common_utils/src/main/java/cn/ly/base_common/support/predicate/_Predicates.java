package cn.ly.base_common.support.predicate;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Created by liaomengge on 2020/6/3.
 */
@UtilityClass
public class _Predicates {

    public <T> Predicate nonNull(T obj) {
        return val -> Objects.nonNull(obj);
    }

    public <T> Predicate isNull(T obj) {
        return val -> Objects.isNull(obj);
    }
}
