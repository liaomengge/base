package com.github.liaomengge.service.base_framework.common.controller;

import com.github.liaomengge.base_common.utils.collection.LyMapUtil;
import com.github.liaomengge.service.base_framework.base.DataResult;
import com.github.liaomengge.service.base_framework.base.code.SystemResultCode;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Created by liaomengge on 2019/11/29.
 */
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributesMap = super.getErrorAttributes(webRequest, options);
        String status = MapUtils.getString(errorAttributesMap, "status");
        String message = MapUtils.getString(errorAttributesMap, "message");
        String error = MapUtils.getString(errorAttributesMap, "error");
        String exception = MapUtils.getString(errorAttributesMap, "exception");

        String sysErrDesc = "HttpStatus[" + status + "], Error Message[" + message + "]";
        String sysException = StringUtils.defaultIfBlank(exception, error);
        DataResult dataResult = DataResult.fail(SystemResultCode.UNKNOWN_ERROR, sysErrDesc, sysException);
        return LyMapUtil.bean2Map4Cglib(dataResult);
    }
}
