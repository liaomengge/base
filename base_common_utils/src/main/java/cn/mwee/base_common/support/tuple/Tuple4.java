package cn.mwee.base_common.support.tuple;

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

    public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, B b, C c, D d) {
        return new Tuple4<>(a, b, c, d);
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
    public <E> Optional<E> _5() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Tuple4{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", d=" + d +
                '}';
    }
}