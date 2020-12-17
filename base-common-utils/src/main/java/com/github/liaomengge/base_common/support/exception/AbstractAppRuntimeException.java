package com.github.liaomengge.base_common.support.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by liaomengge on 17/11/17.
 */
@Setter
@Getter
@ToString
public abstract class AbstractAppRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 446862845631936363L;
    
    protected String errCode;
    protected String errMsg;

    public AbstractAppRuntimeException(String errMsg) {
        super(errMsg);
        this.errMsg = errMsg;
    }

    public AbstractAppRuntimeException(Throwable t) {
        super(t.getMessage(), t);
    }

    public AbstractAppRuntimeException(String errMsg, Throwable t) {
        super(errMsg, t);
        this.errMsg = errMsg;
    }

    public AbstractAppRuntimeException(String errCode, String errMsg) {
        this(errMsg);
        this.errCode = errCode;
    }

    public AbstractAppRuntimeException(String errCode, String errMsg, Throwable t) {
        this(errMsg, t);
        this.errCode = errCode;
    }

    public String getMessage(String defaultMsg) {
        if (StringUtils.isBlank(this.getErrMsg())) {
            return StringUtils.defaultIfBlank(super.getMessage(), defaultMsg);
        }
        return this.getErrMsg();
    }
}