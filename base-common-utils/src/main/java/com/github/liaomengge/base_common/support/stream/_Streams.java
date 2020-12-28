package com.github.liaomengge.base_common.support.stream;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by liaomengge on 2020/12/26.
 */
@UtilityClass
public class _Streams {

    public <T> Stream<T> stream(Iterable<T> iterable) {
        return Optional.ofNullable(iterable).map(val -> {
            if (val instanceof Collection) {
                return ((Collection<T>) val).stream();
            }
            return StreamSupport.stream(val.spliterator(), false);
        }).orElse(Stream.empty());
    }

    public static <T> Stream<T> stream(Iterator<T> iterator) {
        return Optional.ofNullable(iterator)
                .map(val -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(val, 0), false)).orElse(Stream.empty());
    }

    public <C, T extends Collection<C>> Stream<C> stream(T collection) {
        return Optional.ofNullable(collection).map(Collection::stream).orElse(Stream.empty());
    }

    public <T> Stream<T> stream(T[] array) {
        return Optional.ofNullable(array).map(Arrays::stream).orElse(Stream.empty());
    }

    public static <T> Stream<T> stream(Optional<T> optional) {
        return optional.isPresent() ? Stream.of(optional.get()) : Stream.empty();
    }
}
