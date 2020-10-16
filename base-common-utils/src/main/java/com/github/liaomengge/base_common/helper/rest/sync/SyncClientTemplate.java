package com.github.liaomengge.base_common.helper.rest.sync;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.github.liaomengge.base_common.helper.rest.Template;
import com.github.liaomengge.base_common.helper.rest.data.BaseRequest;
import com.github.liaomengge.base_common.utils.error.LyExceptionUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.log.LyAlarmLogUtil;
import com.github.liaomengge.base_common.utils.log.LyMDCUtil;
import com.github.liaomengge.base_common.utils.url.LyUrlUtil;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InterruptedIOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 17/3/9.
 */
@NoArgsConstructor
public class SyncClientTemplate extends Template.Sync {

    @Setter
    private RestTemplate restTemplate;
    @Setter
    private String ignoreLogMethodName;//逗号分隔

    public SyncClientTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SyncClientTemplate(String projName, RestTemplate restTemplate) {
        super(projName);
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(BaseRequest<Map<String, String>> baseRequest, Class<T> responseType) {
        long startTime = System.nanoTime(), tookMs;
        boolean isSuccess = true;
        String prefix = super.getMetricsPrefixName(baseRequest);

        String url = baseRequest.getUrl();
        Map<String, String> dataMap = baseRequest.getData();
        if (MapUtils.isNotEmpty(dataMap)) {
            url = LyUrlUtil.rebuildUrl(url, dataMap);
        }
        ResponseEntity<T> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(url, responseType);
        } catch (Throwable e) {
            isSuccess = false;
            this.handleThrowable(baseRequest, e);
        } finally {
            try {
                long endTime = System.nanoTime();
                tookMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                LyMDCUtil.put(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME, String.valueOf(tookMs));
                if (isSuccess) {
                    if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                        log.info("调用服务成功, 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]", url,
                                TimeUnit.NANOSECONDS.toMillis(endTime - startTime),
                                Optional.ofNullable(responseEntity).map(ResponseEntity::getBody).map(LyJsonUtil::toJson4Log).orElse(null));
                    } else {
                        log.info("调用服务成功, 请求参数[{}], 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]",
                                LyJsonUtil.toJson4Log(baseRequest.getData()), url,
                                TimeUnit.NANOSECONDS.toMillis(endTime - startTime),
                                Optional.ofNullable(responseEntity).map(ResponseEntity::getBody).map(LyJsonUtil::toJson4Log).orElse(null));
                    }
                }
                super.statRestExec(prefix, isSuccess, (endTime - startTime));
            } finally {
                LyMDCUtil.remove(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME);
            }
        }
        return responseEntity;
    }

    @Override
    public <T> ResponseEntity<T> postFormForEntity(BaseRequest<Map<String, String>> baseRequest,
                                                   Class<T> responseType) {
        long startTime = System.nanoTime(), tookMs;
        boolean isSuccess = true;
        String prefix = super.getMetricsPrefixName(baseRequest);
        String url = baseRequest.getUrl();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.setAll(baseRequest.getData());

        HttpEntity httpEntity = new HttpEntity(multiValueMap, httpHeaders);
        ResponseEntity<T> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(url, httpEntity, responseType);
        } catch (Throwable e) {
            isSuccess = false;
            this.handleThrowable(baseRequest, e);
        } finally {
            try {
                long endTime = System.nanoTime();
                tookMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                LyMDCUtil.put(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME, String.valueOf(tookMs));
                if (isSuccess) {
                    if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                        log.info("调用服务成功, 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]", url,
                                TimeUnit.NANOSECONDS.toMillis(endTime - startTime),
                                Optional.ofNullable(responseEntity).map(ResponseEntity::getBody).map(LyJsonUtil::toJson4Log).orElse(null));
                    } else {
                        log.info("调用服务成功, 请求参数[{}], 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]",
                                LyJsonUtil.toJson4Log(baseRequest.getData()), url,
                                TimeUnit.NANOSECONDS.toMillis(endTime - startTime),
                                Optional.ofNullable(responseEntity).map(ResponseEntity::getBody).map(LyJsonUtil::toJson4Log).orElse(null));
                    }
                }
                super.statRestExec(prefix, isSuccess, (endTime - startTime));
            } finally {
                LyMDCUtil.remove(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME);
            }
        }
        return responseEntity;
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(BaseRequest<?> baseRequest, Class<T> responseType) {
        long startTime = System.nanoTime(), tookMs;
        boolean isSuccess = true;
        String prefix = super.getMetricsPrefixName(baseRequest);

        String url = baseRequest.getUrl();
        HttpEntity httpEntity = new HttpEntity(baseRequest.getData());
        ResponseEntity<T> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(url, httpEntity, responseType);
        } catch (Throwable e) {
            isSuccess = false;
            this.handleThrowable(baseRequest, e);
        } finally {
            try {
                long endTime = System.nanoTime();
                tookMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                LyMDCUtil.put(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME, String.valueOf(tookMs));
                if (isSuccess) {
                    if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                        log.info("调用服务成功, 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]", url,
                                TimeUnit.NANOSECONDS.toMillis(endTime - startTime),
                                Optional.ofNullable(responseEntity).map(ResponseEntity::getBody).map(LyJsonUtil::toJson4Log).orElse(null));
                    } else {
                        log.info("调用服务成功, 请求参数[{}], 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]",
                                LyJsonUtil.toJson4Log(baseRequest.getData()), url,
                                TimeUnit.NANOSECONDS.toMillis(endTime - startTime),
                                Optional.ofNullable(responseEntity).map(ResponseEntity::getBody).map(LyJsonUtil::toJson4Log).orElse(null));
                    }
                }
                super.statRestExec(prefix, isSuccess, (endTime - startTime));
            } finally {
                LyMDCUtil.remove(LyMDCUtil.MDC_CLIENT_ELAPSED_MILLI_TIME);
            }
        }
        return responseEntity;
    }

    private void handleThrowable(BaseRequest<?> baseRequest, Throwable t) {
        if (t instanceof InterruptedIOException || (Objects.nonNull(t.getCause()) && t.getCause() instanceof InterruptedIOException)) {
            LyAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_HTTP.error(t);
        } else {
            LyAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_BIZ.error(t);
        }
        String url = baseRequest.getUrl();
        if (t instanceof BlockException || LyExceptionUtil.unwrap(t) instanceof BlockException) {
            BlockException e = (BlockException) LyExceptionUtil.unwrap(t);
            Optional.ofNullable(e).map(BlockException::getRule).map(AbstractRule::getResource).ifPresent(val -> {
                if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                    log.error("调用服务失败, 服务地址[{}], 异常原因 ===> [{}]", url, "[" + e + "] Block Exception...");
                } else {
                    log.error("调用服务失败, 请求参数[{}], 服务地址[{}], 异常原因 ===> [{}]",
                            LyJsonUtil.toJson4Log(baseRequest.getData()), url, "[" + e + "] Block Exception...");
                }
            });
        } else {
            if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                log.error("调用服务失败, 服务地址[{}], 异常原因 ===> [{}]", url, t.getMessage());
            } else {
                log.error("调用服务失败, 请求参数[{}], 服务地址[{}], 异常原因 ===> [{}]",
                        LyJsonUtil.toJson4Log(baseRequest.getData()), url, t.getMessage());
            }
        }
    }
}
