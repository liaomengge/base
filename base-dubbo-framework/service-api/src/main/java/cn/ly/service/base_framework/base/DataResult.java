package cn.ly.service.base_framework.base;

import cn.ly.base_common.utils.string.LyToStringUtil;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataResult<T> implements Serializable {

    private static final long serialVersionUID = -7793112364650981322L;

    public DataResult() {
        this(true);
    }

    public DataResult(boolean success) {
        this.success = success;
    }

    public DataResult(T data) {
        this(true);
        this.data = data;
    }

    public DataResult(String sysCode, String sysMsg) {
        this(false);
        this.sysCode = sysCode;
        this.sysMsg = sysMsg;
    }

    public DataResult(String sysCode, String sysMsg, String sysException) {
        this(sysCode, sysMsg);
        this.sysException = sysException;
    }

    public DataResult(String sysCode, String sysMsg, long elapsedMilliseconds) {
        this(sysCode, sysMsg);
        this.elapsedMilliseconds = elapsedMilliseconds;
    }

    /**
     * 是否处理成功
     */
    private boolean success;

    /**
     * 返回的数据
     */
    private T data;

    /**
     * 错误代码
     */
    private String sysCode = "";

    /**
     * 错误描述
     */
    private String sysMsg = "";

    /**
     * 异常详情
     */
    private String sysException = "";

    /**
     * 处理耗时(毫秒)
     */
    private long elapsedMilliseconds;

    @Override
    public String toString() {
        return LyToStringUtil.toString(this);
    }

}
