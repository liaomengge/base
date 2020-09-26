package cn.ly.base_common.utils.binder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2020/7/4.
 */
@UtilityClass
public class LyBinderUtil {

    public <T> T bind(Environment environment, String prefix, ResolvableType type) {
        Binder binder = Binder.get(environment);
        Bindable<T> bindable = Bindable.of(type);
        BindResult<T> bindResult = binder.bind(prefix, bindable);
        return bindResult.get();
    }

    public <T> T bind(Environment environment, String prefix, Class<T> type) {
        Binder binder = Binder.get(environment);
        Bindable<T> bindable = Bindable.of(type);
        BindResult<T> bindResult = binder.bind(prefix, bindable);
        return bindResult.get();
    }

    public <T> List<T> bindList(Environment environment, String prefix, Class<T> type) {
        Binder binder = Binder.get(environment);
        Bindable<List<T>> bindable = Bindable.listOf(type);
        BindResult<List<T>> bindResult = binder.bind(prefix, bindable);
        return bindResult.get();
    }

    public <T> Set<T> bindSet(Environment environment, String prefix, Class<T> type) {
        Binder binder = Binder.get(environment);
        Bindable<Set<T>> bindable = Bindable.setOf(type);
        BindResult<Set<T>> bindResult = binder.bind(prefix, bindable);
        return bindResult.get();
    }

    public <K, V> Map<K, V> bindMap(Environment environment, String prefix, Class<K> keyType,
                                    Class<V> valueType) {
        Binder binder = Binder.get(environment);
        Bindable<Map<K, V>> bindable = Bindable.mapOf(keyType, valueType);
        BindResult<Map<K, V>> bindResult = binder.bind(prefix, bindable);
        return bindResult.get();
    }
}
