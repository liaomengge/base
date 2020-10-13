package com.github.liaomengge.base_common.support.exception;

/**
 * Created by liaomengge on 16/8/2.
 */
public class CommunicationException extends AbstractAppRuntimeException {


    public CommunicationException(String errMsg) {
        super(errMsg);
    }

    public CommunicationException(Throwable t) {
        super(t);
    }

    public CommunicationException(String errMsg, Throwable t) {
        super(errMsg, t);
    }

    public CommunicationException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public CommunicationException(String errCode, String errMsg, Throwable t) {
        super(errCode, errMsg, t);
    }

    @Override
    public String getMessage() {
        return super.getMessage("通信异常");
    }
}
