package com.github.liaomengge.service.base_framework.common.filter;

import com.alibaba.dubbo.rpc.*;
import com.github.liaomengge.base_common.helper.mail.MailHelper;
import com.github.liaomengge.base_common.support.datasource.DBContext;
import com.github.liaomengge.base_common.support.exception.AbstractAppException;
import com.github.liaomengge.base_common.support.exception.AbstractAppRuntimeException;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.log.LyMDCUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogData;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import com.github.liaomengge.service.base_framework.base.DataResult;
import com.github.liaomengge.service.base_framework.common.consts.ServiceConst;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInputImpl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 16/11/9.
 */
public class ServiceFilter extends AbstractFilter {

    @Setter
    private MailHelper mailHelper;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (SKIP_METHOD.equalsIgnoreCase(invocation.getMethodName())) {
            return invoker.invoke(invocation);
        }

        long startTime = System.nanoTime();
        LyLogData logData = new LyLogData();

        try {
            //1. log params info
            RpcContext rpcContext = RpcContext.getContext();
            this.buildRequestLog(rpcContext, invocation, logData);

            //2. invoke
            Result result = invoker.invoke(invocation);

            long endTime = System.nanoTime();
            long elapsedNanoTime = endTime - startTime;
            logData.setElapsedMilliseconds(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime));
            LyMDCUtil.put(LyMDCUtil.MDC_API_ELAPSED_MILLI_TIME,
                    String.valueOf(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime)));

            boolean hasException = false;
            if (Objects.isNull(result.getValue())) { //无返回,通常是发生了异常,被dubbo捕获了
                DataResult<String> nullResult = new DataResult<>(false);
                nullResult.setElapsedMilliseconds(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime));
                if (result instanceof RpcResult) {
                    RpcResult rpcResult = (RpcResult) result;
                    rpcResult.setValue(nullResult);
                    if (result.hasException()) {
                        hasException = true;
                        logData.setErrorStack(LyThrowableUtil.getStackTrace(result.getException()));
                        String errorCode = ServiceConst.ResponseStatus.ErrorCodeEnum.SERVER_ERROR.getCode();
                        String errorDesc = ServiceConst.ResponseStatus.ErrorCodeEnum.SERVER_ERROR.getMsg();
                        String errorException = logData.getErrorStack();

                        Throwable throwable = rpcResult.getException();
                        if (throwable instanceof AbstractAppException) {
                            errorCode = ((AbstractAppException) throwable).getErrCode();
                            errorDesc = ((AbstractAppException) throwable).getErrMsg();
                        }
                        if (throwable instanceof AbstractAppRuntimeException) {
                            errorCode = ((AbstractAppRuntimeException) throwable).getErrCode();
                            errorDesc = ((AbstractAppRuntimeException) throwable).getErrMsg();
                        }

                        nullResult.setSysCode(errorCode);
                        nullResult.setSysMsg(errorDesc);
                        if (serviceConfig.isThrowException()) {
                            nullResult.setSysException(errorException);
                        }

                        rpcResult.setException(null);
                    }
                }
                this.handleException(logData, hasException);
                return result;
            }

            //3. log result
            logData.setResult(result.toString());

            if (result.getValue() instanceof DataResult) {
                DataResult dt = (DataResult) result.getValue();
                dt.setElapsedMilliseconds(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime));
                if (!dt.isSuccess()) {
                    hasException = true;
                }
            }

            this.handleException(logData, hasException);

            return result;

        } catch (Exception e) {
            logData.setErrorStack(LyThrowableUtil.getStackTrace(e));
            //其它未知异常处理
            DataResult dataResult = new DataResult(ServiceConst.ResponseStatus.ErrorCodeEnum.SERVER_ERROR.getCode(),
                    ServiceConst.ResponseStatus.ErrorCodeEnum.SERVER_ERROR.getMsg());
            if (serviceConfig.isThrowException()) {
                dataResult.setSysException(LyThrowableUtil.getStackTrace(e));
            }
            RpcResult result = new RpcResult(dataResult);
            result.setAttachments(invocation.getAttachments());
            logData.setResult(result.toString());

            long endTime = System.nanoTime();
            long elapsedNanoTime = endTime - startTime;
            LyMDCUtil.put(LyMDCUtil.MDC_API_ELAPSED_MILLI_TIME,
                    String.valueOf(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime)));

            logData.setElapsedMilliseconds(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime));
            dataResult.setElapsedMilliseconds(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime));

            log.error(logData);

            return result;
        } finally {
            //4. clear db thread local
            DBContext.clearDBKey();

            //5. clear trace thread local
            LyTraceLogUtil.clearTrace();

            //6. clean mdc
            LyMDCUtil.remove(LyMDCUtil.MDC_API_ELAPSED_MILLI_TIME);
        }

    }

    private void buildRequestLog(RpcContext rpcContext, Invocation invocation, LyLogData logData) {
        HttpServletRequest servletRequest = (HttpServletRequest) rpcContext.getRequest();

        //1. 获取nginx http的真实IP
        String realIp = servletRequest.getHeader("X-Real-IP");
        if (!StringUtils.isEmpty(realIp) && !realIp.equals(logData.getRemoteIp())) {
            logData.setRemoteIp(realIp);
        }

        //2. 获取请求的url&ip
        logData.setRestUrl(servletRequest.getRequestURL().toString());
        logData.setRemoteIp(rpcContext.getRemoteAddressString());
        logData.setHostIp(rpcContext.getLocalAddressString());

        //3. 获取请求的参数
        if (this.isIgnoreLogMethod(invocation)) {
            return;
        }
        String methodName = invocation.getMethodName();
        String method = servletRequest.getMethod();
        if (HttpMethod.POST.equalsIgnoreCase(method)) {
            logData.setInvocation(invocation.toString());
            String contentType = StringUtils.defaultString(servletRequest.getContentType());
            if (contentType.contains(MediaType.APPLICATION_JSON)) {
                return;
            }
            if (contentType.contains(MediaType.MULTIPART_FORM_DATA)) {
                Map<String, Object> queryParamMap = this.getRequestParams(invocation);
                if (MapUtils.isEmpty(queryParamMap)) {
                    logData.setInvocation(invocation.toString());
                    return;
                }
                logData.setInvocation("RpcInvocation[methodName=" + methodName + "], arguments=" + queryParamMap.toString());
                return;
            }
        }

        Map<String, Object> queryParamMap = LyWebUtil.getRequestParams(servletRequest);
        logData.setInvocation("RpcInvocation[methodName=" + methodName + "], arguments=" + queryParamMap.toString());
    }

    private boolean isIgnoreLogMethod(Invocation invocation) {
        String ignoreLogMethodName = filterConfig.getIgnoreLogMethodName();
        if (StringUtils.isNotBlank(ignoreLogMethodName)) {
            String methodName = invocation.getMethodName();
            Iterable<String> iterable =
                    Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings().split(ignoreLogMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    private Map<String, Object> getRequestParams(Invocation invocation) {
        Map<String, Object> paramMap = Maps.newHashMap();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        if (parameterTypes != null && parameterTypes.length > 0) {
            if (MultipartFormDataInput.class.isAssignableFrom(parameterTypes[0])) {
                MultipartFormDataInputImpl multipartFormDataInput =
                        (MultipartFormDataInputImpl) invocation.getArguments()[0];
                Map<String, List<InputPart>> inputPartMap = multipartFormDataInput.getFormDataMap();
                inputPartMap.entrySet().stream().forEach(entry -> {
                    List<InputPart> inputPartList = entry.getValue();
                    if (CollectionUtils.isNotEmpty(inputPartList)) {
                        try {
                            paramMap.put(entry.getKey(), inputPartList.get(0).getBodyAsString());
                        } catch (IOException e) {
                            paramMap.put(entry.getKey(), "");
                        }
                    }
                });
            }
        }
        return paramMap;
    }

    private void handleException(LyLogData logData, boolean hasException) {
        if (hasException) {
            log.error(logData);
            if (serviceConfig.isSendEmail() && mailHelper != null) {
                mailHelper.sendTextMail(LyNetworkUtil.getHostAddress() + "/" + LyNetworkUtil.getHostName() +
                        "-[" + serviceConfig.getServiceName() + "] ERROR!", LyJsonUtil.toJson4Log(logData));
            }
            return;
        }
        log.info(logData);
    }
}
