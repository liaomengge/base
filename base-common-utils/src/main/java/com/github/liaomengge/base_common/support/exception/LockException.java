package com.github.liaomengge.base_common.support.exception;

/**
 * Created by liaomengge on 2018/11/29.
 */
public class LockException extends AbstractAppRuntimeException {

    private static final long serialVersionUID = -2078255553870810196L;

    public LockException(String errMsg) {
        super(errMsg);
    }

    public LockException(Throwable t) {
        super(t);
    }

    public LockException(String errMsg, Throwable t) {
        super(errMsg, t);
    }

    public LockException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public LockException(String errCode, String errMsg, Throwable t) {
        super(errCode, errMsg, t);
    }

    @Override
    public String getMessage() {
        return super.getMessage("lock exception");
    }
}
