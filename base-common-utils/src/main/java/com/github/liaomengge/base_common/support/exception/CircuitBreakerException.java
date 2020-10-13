package com.github.liaomengge.base_common.support.exception;

/**
 * Created by liaomengge on 2019/6/27.
 */
public class CircuitBreakerException extends AbstractAppRuntimeException {

    public CircuitBreakerException(String errMsg) {
        super(errMsg);
    }

    public CircuitBreakerException(Throwable t) {
        super(t);
    }

    public CircuitBreakerException(String errMsg, Throwable t) {
        super(errMsg, t);
    }

    public CircuitBreakerException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public CircuitBreakerException(String errCode, String errMsg, Throwable t) {
        super(errCode, errMsg, t);
    }

    @Override
    public String getMessage() {
        return super.getMessage("熔断异常");
    }
}
