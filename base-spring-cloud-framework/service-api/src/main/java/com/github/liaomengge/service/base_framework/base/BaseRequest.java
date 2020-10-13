package com.github.liaomengge.service.base_framework.base;

import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by liaomengge on 17/9/29.
 */
@Data
public abstract class BaseRequest<T> implements Serializable {

    private static final long serialVersionUID = 4225161823322032929L;

    private String appId;
    private String sign;
    private String language = "zh-CN";
    private String timeZone = "GMT+8";
    private Long timestamp = LyJdk8DateUtil.getSecondTime();

    @Valid
    @NotNull(message = "data不能为空")
    private T data;
}
