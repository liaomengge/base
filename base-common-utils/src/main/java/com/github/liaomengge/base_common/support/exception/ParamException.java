package com.github.liaomengge.base_common.support.exception;


public class ParamException extends AbstractAppException {

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
        return super.getMessage("参数错误");
    }
}