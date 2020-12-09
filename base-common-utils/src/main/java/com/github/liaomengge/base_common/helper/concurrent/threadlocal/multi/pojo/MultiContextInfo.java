package com.github.liaomengge.base_common.helper.concurrent.threadlocal.multi.pojo;

import lombok.Data;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Map;

/**
 * Created by liaomengge on 2020/12/8.
 */
@Data
public class MultiContextInfo {

    private Map<String, String> mdcContext;
    private Map<String, Object> mapContext;
    private RequestAttributes requestAttributesContext;
}
