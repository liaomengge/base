package cn.ly.base_common.helper.rest.sync;

import cn.ly.base_common.helper.rest.Template;
import cn.ly.base_common.utils.error.MwExceptionUtil;
import cn.ly.base_common.utils.json.MwJsonUtil;
import cn.ly.base_common.utils.log.MwAlarmLogUtil;
import cn.ly.base_common.utils.log.MwMDCUtil;
import cn.ly.base_common.utils.url.MwUrlUtil;
import cn.ly.base_common.helper.rest.data.BaseRequest;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;
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
        long startTime = System.currentTimeMillis();
        boolean isSuccess = true;
        String prefix = super.getMetricsPrefixName(baseRequest);

        String url = baseRequest.getUrl();
        Map<String, String> dataMap = baseRequest.getData();
        if (MapUtils.isNotEmpty(dataMap)) {
            url = MwUrlUtil.rebuildUrl(url, dataMap);
        }
        ResponseEntity<T> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(url, responseType);
        } catch (Throwable e) {
            isSuccess = false;
            this.handleThrowable(baseRequest, e);
        } finally {
            try {
                long endTime = System.currentTimeMillis();
                MwMDCUtil.put(MwMDCUtil.MDC_THIRD_ELAPSED_TIME, String.valueOf(endTime - startTime));
                if (isSuccess) {
                    if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                        logger.info("调用服务成功, 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]", url, (endTime - startTime),
                                Objects.isNull(responseEntity) ? null :
                                        MwJsonUtil.toJson4Log(responseEntity.getBody()));
                    } else {
                        logger.info("调用服务成功, 请求参数[{}], 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]",
                                MwJsonUtil.toJson4Log(baseRequest.getData()), url, (endTime - startTime),
                                Objects.isNull(responseEntity) ? null :
                                        MwJsonUtil.toJson4Log(responseEntity.getBody()));
                    }
                }
                super.statRestExec(prefix, isSuccess, (endTime - startTime));
            } finally {
                MwMDCUtil.remove(MwMDCUtil.MDC_THIRD_ELAPSED_TIME);
            }
        }
        return responseEntity;
    }

    @Override
    public <T> ResponseEntity<T> postFormForEntity(BaseRequest<Map<String, String>> baseRequest,
                                                   Class<T> responseType) {
        long startTime = System.currentTimeMillis();
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
                long endTime = System.currentTimeMillis();
                MwMDCUtil.put(MwMDCUtil.MDC_THIRD_ELAPSED_TIME, String.valueOf(endTime - startTime));
                if (isSuccess) {
                    if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                        logger.info("调用服务成功, 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]", url, (endTime - startTime),
                                Objects.isNull(responseEntity) ? null :
                                        MwJsonUtil.toJson4Log(responseEntity.getBody()));
                    } else {
                        logger.info("调用服务成功, 请求参数[{}], 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]",
                                MwJsonUtil.toJson4Log(baseRequest.getData()), url, (endTime - startTime),
                                Objects.isNull(responseEntity) ? null :
                                        MwJsonUtil.toJson4Log(responseEntity.getBody()));
                    }
                }
                super.statRestExec(prefix, isSuccess, (endTime - startTime));
            } finally {
                MwMDCUtil.remove(MwMDCUtil.MDC_THIRD_ELAPSED_TIME);
            }
        }
        return responseEntity;
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(BaseRequest<?> baseRequest, Class<T> responseType) {
        long startTime = System.currentTimeMillis();
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
                long endTime = System.currentTimeMillis();
                MwMDCUtil.put(MwMDCUtil.MDC_THIRD_ELAPSED_TIME, String.valueOf(endTime - startTime));
                if (isSuccess) {
                    if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                        logger.info("调用服务成功, 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]", url, (endTime - startTime),
                                Objects.isNull(responseEntity) ? null :
                                        MwJsonUtil.toJson4Log(responseEntity.getBody()));
                    } else {
                        logger.info("调用服务成功, 请求参数[{}], 服务地址[{}], 耗时[{}]ms, 返回结果 ===> [{}]",
                                MwJsonUtil.toJson4Log(baseRequest.getData()), url, (endTime - startTime),
                                Objects.isNull(responseEntity) ? null :
                                        MwJsonUtil.toJson4Log(responseEntity.getBody()));
                    }
                }
                super.statRestExec(prefix, isSuccess, (endTime - startTime));
            } finally {
                MwMDCUtil.remove(MwMDCUtil.MDC_THIRD_ELAPSED_TIME);
            }
        }
        return responseEntity;
    }

    private void handleThrowable(BaseRequest<?> baseRequest, Throwable t) {
        if (t instanceof InterruptedIOException || (Objects.nonNull(t.getCause()) && t.getCause() instanceof InterruptedIOException)) {
            MwAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_HTTP.error(t);
        } else {
            MwAlarmLogUtil.ClientProjEnum.BASE_PREFIX_CALLER_BIZ.error(t);
        }
        String url = baseRequest.getUrl();
        if (t instanceof BlockException || MwExceptionUtil.unwrap(t) instanceof BlockException) {
            BlockException e = (BlockException) MwExceptionUtil.unwrap(t);
            Optional.ofNullable(e).map(BlockException::getRule).map(AbstractRule::getResource).ifPresent(val -> {
                if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                    logger.error("调用服务失败, 服务地址[{}], 异常原因 ===> [{}]", url, "[" + e + "] Block Exception...");
                } else {
                    logger.error("调用服务失败, 请求参数[{}], 服务地址[{}], 异常原因 ===> [{}]",
                            MwJsonUtil.toJson4Log(baseRequest.getData()), url, "[" + e + "] Block Exception...");
                }
            });
        } else {
            if (isIgnoreLogMethod(url, ignoreLogMethodName)) {
                logger.error("调用服务失败, 服务地址[{}], 异常原因 ===> [{}]", url, t.getMessage());
            } else {
                logger.error("调用服务失败, 请求参数[{}], 服务地址[{}], 异常原因 ===> [{}]",
                        MwJsonUtil.toJson4Log(baseRequest.getData()), url, t.getMessage());
            }
        }
    }
}
