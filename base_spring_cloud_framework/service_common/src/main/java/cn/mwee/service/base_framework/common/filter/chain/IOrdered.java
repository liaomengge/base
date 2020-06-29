package cn.mwee.service.base_framework.common.filter.chain;

import org.springframework.core.Ordered;

/**
 * Created by liaomengge on 2018/11/21.
 */
public interface IOrdered extends Ordered {

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
