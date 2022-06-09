package com.github.liaomengge.base_common.support.tuple;

import java.util.Optional;

public class Tuple4<A, B, C, D> extends Tuple {
    private A a;
    private B b;
    private C c;
    private D d;

    Tuple4(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Optional<A> _1() {
        return Optional.ofNullable(a);
    }

    public Optional<B> _2() {
        return Optional.ofNullable(b);
    }

    public Optional<C> _3() {
        return Optional.ofNullable(c);
    }

    public Optional<D> _4() {
        return Optional.ofNullable(d);
    }

    @Override
    public int arity() {
        return 4;
    }
}