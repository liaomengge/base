package com.github.liaomengge.base_common.support.exception;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by liaomengge on 17/11/17.
 */
@Setter
@Getter
@ToString
public abstract class AbstractAppException extends Exception {

    private static final long serialVersionUID = 2433471215312281752L;
    protected String errCode;
    protected String errMsg;

    public AbstractAppException(String errMsg) {
        super(errMsg);
        this.errMsg = errMsg;
    }

    public AbstractAppException(Throwable t) {
        super(t.getMessage(), t);
    }

    public AbstractAppException(String errMsg, Throwable t) {
        super(errMsg, t);
        this.errMsg = errMsg;
    }

    public AbstractAppException(String errCode, String errMsg) {
        this(errMsg);
        this.errCode = errCode;
    }

    public AbstractAppException(String errCode, String errMsg, Throwable t) {
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
