package com.github.liaomengge.base_common.support.tuple;

import java.util.Optional;

public class Tuple2<A, B> extends Tuple {
    private A a;
    private B b;

    Tuple2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public Optional<A> _1() {
        return Optional.ofNullable(a);
    }

    public Optional<B> _2() {
        return Optional.ofNullable(b);
    }

    @Override
    public int arity() {
        return 2;
    }
}