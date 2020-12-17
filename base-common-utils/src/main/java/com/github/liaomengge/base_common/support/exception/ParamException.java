package com.github.liaomengge.base_common.support.exception;


public class ParamException extends AbstractAppException {

    private static final long serialVersionUID = 7160731759493165193L;

    public ParamException(String errMsg) {
        super(errMsg);
    }

    public ParamException(Throwable t) {
        super(t);
    }

    public ParamException(String errMsg, Throwable t) {
        super(errMsg, t);
    }

    public ParamException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public ParamException(String errCode, String errMsg, Throwable t) {
        super(errCode, errMsg, t);
    }

    @Override
    public String getMessage() {
        return super.getMessage("param exception");
    }
}