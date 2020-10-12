package cn.ly.base_common.support.meter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * Created by liaomengge on 2020/10/12.
 */
@UtilityClass
public class _MeterRegistrys {

    public void counter(MeterRegistry meterRegistry, String name, Iterable<Tag> tags) {
        Optional.ofNullable(meterRegistry).ifPresent(val -> val.counter(name, tags).increment());
    }

    public void counter(MeterRegistry meterRegistry, String name, String... tags) {
        Optional.ofNullable(meterRegistry).ifPresent(val -> val.counter(name, tags).increment());
    }
}
