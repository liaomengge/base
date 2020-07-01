package cn.ly.service.base_framework.base;

import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by liaomengge on 17/9/29.
 */
@Data
public abstract class BaseRestRequest<T> implements Serializable {

    private static final long serialVersionUID = 4225161823322032929L;

    @NotBlank(message = "appId不能为空!")
    private String appId;

    @NotBlank(message = "language不能为空!")
    private String language = "zh-CN";

    private String requestId;

    @NotBlank(message = "sign不能为空!")
    private String sign;

    @NotBlank(message = "timeZone不能为空!")
    private String timeZone = "GMT+8";

    @Min(value = 1)
    private Long timestamp = LyJdk8DateUtil.getSecondTime();

    @Valid
    @NotNull(message = "data不能为空")
    private T data;
}
