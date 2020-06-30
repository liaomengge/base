package cn.ly.service.base_framework.common.filter;

import cn.ly.service.base_framework.base.BaseRestRequest;
import cn.ly.service.base_framework.base.DataResult;
import cn.ly.service.base_framework.base.StringRestResponse;
import cn.ly.service.base_framework.base.code.SystemResultCode;
import cn.ly.base_common.utils.json.MwJsonUtil;
import cn.ly.base_common.utils.sign.MwSignUtil;
import cn.ly.service.base_framework.common.config.FilterConfig;
import cn.ly.service.base_framework.common.filter.chain.FilterChain;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static cn.ly.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2018/11/22.
 */
@Order(1000)
@AllArgsConstructor
public class SignFilter extends AbstractFilter {

    private final FilterConfig filterConfig;

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (Objects.isNull(args) || args.length <= 0 || isDisableAndIgnoreMethodName(joinPoint)) {
            return chain.doFilter(joinPoint, chain);
        }
        for (Object obj : args) {
            if (obj instanceof BaseRestRequest) {
                if (isSignOk((BaseRestRequest) obj)) {
                    return chain.doFilter(joinPoint, chain);
                }
            }
        }
        DataResult<StringRestResponse> dataResult = new DataResult<>(false);

        StringRestResponse restResponse = new StringRestResponse();
        restResponse.setErrNo(SystemResultCode.SIGN_ERROR.getCode());
        restResponse.setErrMsg(SystemResultCode.SIGN_ERROR.getDescription());
        dataResult.setData(restResponse);
        return dataResult;
    }

    private boolean isDisableAndIgnoreMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        FilterConfig.SignConfig signConfig = filterConfig.getSign();
        return !signConfig.isEnabled() || SPLITTER.splitToList(signConfig.getIgnoreMethodName()).contains(methodName);
    }

    private boolean isSignOk(BaseRestRequest signRequest) {
        TreeMap<String, Object> paramsMap = new TreeMap<>();

        String appId = signRequest.getAppId();
        paramsMap.put("appId", appId);
        paramsMap.put("language", signRequest.getLanguage());
        paramsMap.put("requestId", signRequest.getRequestId());
        paramsMap.put("timeZone", signRequest.getTimeZone());
        paramsMap.put("timestamp", signRequest.getTimestamp());

        String signConfig = filterConfig.getSign().getConfig();
        Map<String, Object> signConfigMap;
        try {
            signConfigMap = MwJsonUtil.fromJson(signConfig, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return false;
        }
        String appKey = MapUtils.getString(signConfigMap, appId);

        return MwSignUtil.sign(appKey, paramsMap, "appKey").equals(signRequest.getSign());
    }
}
