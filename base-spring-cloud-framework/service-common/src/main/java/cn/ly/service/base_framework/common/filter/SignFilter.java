package cn.ly.service.base_framework.common.filter;

import static cn.ly.base_common.support.misc.consts.ToolConst.SPLITTER;

import cn.ly.base_common.utils.json.LyJacksonUtil;
import cn.ly.base_common.utils.sign.LySignUtil;
import cn.ly.service.base_framework.base.BaseRequest;
import cn.ly.service.base_framework.base.DataResult;
import cn.ly.service.base_framework.base.code.SystemResultCode;
import cn.ly.service.base_framework.common.config.FilterConfig;
import cn.ly.service.base_framework.common.filter.chain.FilterChain;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.collections4.MapUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import lombok.AllArgsConstructor;

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
            if (obj instanceof BaseRequest) {
                if (isSignOk((BaseRequest) obj)) {
                    return chain.doFilter(joinPoint, chain);
                }
            }
        }
        return DataResult.fail(SystemResultCode.SIGN_ERROR);
    }

    private boolean isDisableAndIgnoreMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        FilterConfig.SignConfig signConfig = filterConfig.getSign();
        return !signConfig.isEnabled() || SPLITTER.splitToList(signConfig.getIgnoreMethodName()).contains(methodName);
    }

    private boolean isSignOk(BaseRequest signRequest) {
        TreeMap<String, Object> paramsMap = new TreeMap<>();

        String appId = signRequest.getAppId();
        paramsMap.put("appId", appId);
        paramsMap.put("language", signRequest.getLanguage());
        paramsMap.put("timeZone", signRequest.getTimeZone());
        paramsMap.put("timestamp", signRequest.getTimestamp());

        String signConfig = filterConfig.getSign().getConfig();
        Map<String, Object> signConfigMap;
        try {
            signConfigMap = LyJacksonUtil.fromJson(signConfig, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return false;
        }
        String appKey = MapUtils.getString(signConfigMap, appId);

        return LySignUtil.sign(appKey, paramsMap, "appKey").equals(signRequest.getSign());
    }
}
