package cn.ly.base_common.support.tuple;

import java.util.Optional;

/**
 * Tuple元组类
 *
 * @since: 12/4/15.
 * @author: http://yjmyzz.cnblogs.com/
 */
public abstract class Tuple {

    public abstract <A> Optional<A> _1();

    public abstract <B> Optional<B> _2();

    public abstract <C> Optional<C> _3();

    public abstract <D> Optional<D> _4();

    public abstract <E> Optional<E> _5();
}
