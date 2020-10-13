package com.github.liaomengge.base_common.support.tuple;

import java.util.Optional;

public class Tuple5<A, B, C, D, E> extends Tuple {
    private A a;
    private B b;
    private C c;
    private D d;
    private E e;

    Tuple5(A a, B b, C c, D d, E e) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(A a, B b, C c, D d, E e) {
        return new Tuple5<>(a, b, c, d, e);
    }

    @Override
    public Optional<A> _1() {
        return Optional.ofNullable(a);
    }

    @Override
    public Optional<B> _2() {
        return Optional.ofNullable(b);
    }

    @Override
    public Optional<C> _3() {
        return Optional.ofNullable(c);
    }

    @Override
    public Optional<D> _4() {
        return Optional.ofNullable(d);
    }

    @Override
    public Optional<E> _5() {
        return Optional.ofNullable(e);
    }

    @Override
    public String toString() {
        return "Tuple5{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", d=" + d +
                ", e=" + e +
                '}';
    }
}