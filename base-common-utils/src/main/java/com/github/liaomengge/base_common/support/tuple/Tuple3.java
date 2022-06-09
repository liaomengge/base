package com.github.liaomengge.base_common.support.tuple;

import java.util.Optional;

public class Tuple3<A, B, C> extends Tuple {
    private A a;
    private B b;
    private C c;

    Tuple3(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
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

    @Override
    public int arity() {
        return 3;
    }
}