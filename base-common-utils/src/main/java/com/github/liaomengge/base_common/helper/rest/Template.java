package com.github.liaomengge.base_common.helper.rest;

import com.github.liaomengge.base_common.helper.rest.consts.ReqMetricsConst;
import com.github.liaomengge.base_common.helper.rest.data.BaseRequest;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.url.LyMoreUrlUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Created by liaomengge on 17/3/9.
 */
public interface Template {

    Logger log = LyLogger.getInstance(Template.class);

    @NoArgsConstructor
    abstract class Sync implements Template {

        @Setter
        private MeterRegistry meterRegistry;

        @Setter
        private String projName = "base-application";

        public Sync(String projName) {
            this.projName = projName;
        }

        public abstract <T> ResponseEntity<T> getForEntity(BaseRequest<Map<String, String>> baseRequest,
                                                           Class<T> responseType);

        public abstract <T> ResponseEntity<T> postFormForEntity(BaseRequest<Map<String, String>> baseRequest,
                                                                Class<T> responseType);

        public abstract <T> ResponseEntity<T> postForEntity(BaseRequest<?> baseRequest, Class<T> responseType);

        protected boolean isIgnoreLogMethod(String url, String ignoreLogMethodName) {
            if (StringUtils.isNotBlank(ignoreLogMethodName)) {
                String methodName = LyMoreUrlUtil.getUrlSuffix(url);
                Iterable<String> iterable =
                        Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings().split(ignoreLogMethodName);
                return Iterables.contains(iterable, methodName);
            }
            return false;
        }

        protected String getMetricsPrefixName(BaseRequest<?> baseRequest) {
            String url = baseRequest.getUrl();
            String urlMethod = LyMoreUrlUtil.getUrlSuffix(url);
            return projName + "." + urlMethod;
        }

        protected void statRestExec(String prefix, boolean isSuccess, long elapsedNanoTime) {
            Optional.ofNullable(meterRegistry).ifPresent(val -> {
                if (isSuccess) {
                    val.counter(prefix + ReqMetricsConst.REQ_EXE_SUC).increment();
                } else {
                    val.counter(prefix + ReqMetricsConst.REQ_EXE_FAIL).increment();
                }
                val.timer(prefix + ReqMetricsConst.REQ_EXE_TIME).record(Duration.ofNanos(elapsedNanoTime));
            });
        }
    }

    abstract class Async implements Template {

        public abstract <T> ListenableFuture<ResponseEntity<T>> postForEntity(BaseRequest<?> baseRequest,
                                                                              Class<T> responseType);
    }

}
