package cn.ly.service.base_framework.common.filter;

import cn.ly.base_common.utils.json.LyJacksonUtil;
import cn.ly.base_common.utils.log4j2.LyLogData;
import cn.ly.base_common.utils.sign.LySignUtil;
import cn.ly.service.base_framework.base.BaseRequest;
import cn.ly.service.base_framework.base.DataResult;
import cn.ly.service.base_framework.common.consts.MetricsConst;
import cn.ly.service.base_framework.common.consts.ServiceConst;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.*;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by liaomengge on 16/11/9.
 */
public class SignFilter extends AbstractFilter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String methodName = invocation.getMethodName();
        if (SKIP_METHOD.equalsIgnoreCase(methodName) || !serviceConfig.isCheckSign()) {
            return invoker.invoke(invocation);
        }

        String signConfig = filterConfig.getSignConfig();
        if (StringUtils.isBlank(signConfig)) {
            return invoker.invoke(invocation);
        }

        Object[] args = invocation.getArguments();
        BaseRequest signRequest;
        if (ArrayUtils.isNotEmpty(args)) {
            signRequest = (BaseRequest) invocation.getArguments()[0];
            if (signRequest != null) {
                if (this.isSignOk(signRequest)) {
                    return invoker.invoke(invocation);
                }
            }
        }

        LyLogData logData = new LyLogData();
        logData.setInvocation(invocation.toString());

        RpcContext rpcContext = RpcContext.getContext();
        logData.setRemoteIp(rpcContext.getRemoteAddressString());
        logData.setHostIp(rpcContext.getLocalAddressString());

        Map<String, Object> rpcResponseMap = Maps.newHashMap();
        rpcResponseMap.put("code", ServiceConst.ResponseStatus.ErrorCodeEnum.SIGN_ERROR.getCode());
        rpcResponseMap.put("msg", ServiceConst.ResponseStatus.ErrorCodeEnum.SIGN_ERROR.getMsg());

        DataResult<Map<String, Object>> dataResult = new DataResult<>(rpcResponseMap);
        RpcResult result = new RpcResult(dataResult);

        URL url = invoker.getUrl();
        String protocol = url.getProtocol();
        String prefix = super.getMetricsPrefixName() + "." + methodName;
        statsDClient.increment(prefix + "." + protocol + MetricsConst.SIGN_EXE_FAIL);

        logData.setResult(result.getValue());
        logData.setRestUrl(url.getAbsolutePath());
        log.info(logData);

        return result;
    }

    private boolean isSignOk(BaseRequest signRequest) {
        TreeMap<String, Object> paramsMap = new TreeMap<>();

        String appId = signRequest.getAppId();
        paramsMap.put("appId", appId);
        paramsMap.put("language", signRequest.getLanguage());
        paramsMap.put("timeZone", signRequest.getTimeZone());
        paramsMap.put("timestamp", signRequest.getTimestamp());

        String signConfig = filterConfig.getSignConfig();
        Map<String, Object> signConfigMap;
        try {
            signConfigMap = LyJacksonUtil.fromJson(signConfig, Map.class);
        } catch (Exception e) {
            return false;
        }
        String appKey = MapUtils.getString(signConfigMap, appId);

        return LySignUtil.sign(appKey, paramsMap, "appKey").equals(signRequest.getSign());
    }

}
