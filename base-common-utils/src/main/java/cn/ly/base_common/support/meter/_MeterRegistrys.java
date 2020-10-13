package cn.ly.base_common.support.meter;

import io.micrometer.core.instrument.*;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Created by liaomengge on 2020/10/12.
 */
@UtilityClass
public class _MeterRegistrys {

    public <U> Optional<U> meter(MeterRegistry meterRegistry, Function<? super MeterRegistry, ? extends U> function) {
        return Optional.ofNullable(meterRegistry).map(function);
    }

    public Optional<Counter> counter(MeterRegistry meterRegistry, String name, Iterable<Tag> tags) {
        return meter(meterRegistry, val -> val.counter(name, tags));
    }

    public Optional<Counter> counter(MeterRegistry meterRegistry, String name, String... tags) {
        return meter(meterRegistry, val -> val.counter(name, tags));
    }

    public Optional<DistributionSummary> summary(MeterRegistry meterRegistry, String name, Iterable<Tag> tags) {
        return meter(meterRegistry, val -> val.summary(name, tags));
    }

    public Optional<DistributionSummary> summary(MeterRegistry meterRegistry, String name, String... tags) {
        return meter(meterRegistry, val -> val.summary(name, tags));
    }

    public Optional<Timer> timer(MeterRegistry meterRegistry, String name, Iterable<Tag> tags) {
        return meter(meterRegistry, val -> val.timer(name, tags));
    }

    public Optional<Timer> timer(MeterRegistry meterRegistry, String name, String... tags) {
        return meter(meterRegistry, val -> val.timer(name, tags));
    }

    public <T extends Number> Optional<T> gauge(MeterRegistry meterRegistry, String name, T t) {
        return gauge(meterRegistry, name, t, Number::doubleValue);
    }

    public <T extends Number> Optional<T> gauge(MeterRegistry meterRegistry, String name, Iterable<Tag> tags, T t) {
        return gauge(meterRegistry, name, tags, t, Number::doubleValue);
    }

    public <T extends Collection<?>> Optional<T> gaugeCollectionSize(MeterRegistry meterRegistry, String name,
                                                                     Iterable<Tag> tags, T t) {
        return gauge(meterRegistry, name, tags, t, Collection::size);
    }

    public <T extends Map<?, ?>> Optional<T> gaugeMapSize(MeterRegistry meterRegistry, String name,
                                                          Iterable<Tag> tags, T t) {
        return gauge(meterRegistry, name, tags, t, Map::size);
    }

    public <T> Optional<T> gauge(MeterRegistry meterRegistry, String name, T t, ToDoubleFunction<T> function) {
        return Optional.ofNullable(meterRegistry).map(val -> val.gauge(name, Collections.emptyList(), t, function));
    }

    public <T> Optional<T> gauge(MeterRegistry meterRegistry, String name, Iterable<Tag> tags, T t,
                                 ToDoubleFunction<T> function) {
        return Optional.ofNullable(meterRegistry).map(val -> val.gauge(name, tags, t, function));
    }
}
