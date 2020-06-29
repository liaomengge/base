package cn.mwee.base_common.dayu.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by liaomengge on 2019/10/31.
 */
@Data
public class DayuBlockedDomain implements Serializable {

    private static final long serialVersionUID = 8056412049999830859L;

    private String sysErrCode = "000800";
    private String sysErrDesc = "服务器繁忙,请稍后再试";

    private DayuBlockedDomain() {
    }

    public static DayuBlockedDomain create() {
        return new DayuBlockedDomain();
    }
}
