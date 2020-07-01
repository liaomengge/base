package cn.ly.service.base_framework.common.controller;

import cn.ly.base_common.utils.collection.LyMapUtil;
import cn.ly.service.base_framework.base.DataResult;
import cn.ly.service.base_framework.base.code.SystemResultCode;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Map;

/**
 * Created by liaomengge on 2019/11/29.
 */
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        Map<String, Object> errorAttributesMap = super.getErrorAttributes(requestAttributes, includeStackTrace);
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
