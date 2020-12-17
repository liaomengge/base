package com.github.liaomengge.base_common.support.exception;

public class BusinessException extends AbstractAppException {

    private static final long serialVersionUID = -2687547527140780955L;

    public BusinessException(String errMsg) {
        super(errMsg);
    }

    public BusinessException(Throwable t) {
        super(t);
    }

    public BusinessException(String errMsg, Throwable t) {
        super(errMsg, t);
    }

    public BusinessException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public BusinessException(String errCode, String errMsg, Throwable t) {
        super(errCode, errMsg, t);
    }

    @Override
    public String getMessage() {
        return super.getMessage("bussiness exception");
    }
}
