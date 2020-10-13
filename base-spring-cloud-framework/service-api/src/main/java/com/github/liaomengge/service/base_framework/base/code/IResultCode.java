package com.github.liaomengge.service.base_framework.base.code;

import java.io.Serializable;

/**
 * Created by liaomengge on 2019/5/24.
 */
public interface IResultCode extends Serializable {

    String getCode();

    String getMsg();
}
