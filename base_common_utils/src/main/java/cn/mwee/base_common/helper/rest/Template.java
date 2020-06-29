package cn.mwee.base_common.helper.rest;

import cn.mwee.base_common.helper.rest.consts.RestMetricsConst;
import cn.mwee.base_common.helper.rest.data.BaseRequest;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import cn.mwee.base_common.utils.url.MwMoreUrlUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.timgroup.statsd.StatsDClient;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;

/**
 * Created by liaomengge on 17/3/9.
 */
public interface Template {

    Logger logger = MwLogger.getInstance(Template.class);

    @NoArgsConstructor
    abstract class Sync implements Template {

        @Setter
        private StatsDClient statsDClient;

        @Setter
        private String projName = "mwee";

        public Sync(String projName) {
            this.projName = projName;
        }

        public abstract <T> ResponseEntity<T> getForEntity(BaseRequest<Map<String, String>> baseRequest, Class<T> responseType);

        public abstract <T> ResponseEntity<T> postFormForEntity(BaseRequest<Map<String, String>> baseRequest, Class<T> responseType);

        public abstract <T> ResponseEntity<T> postForEntity(BaseRequest<?> baseRequest, Class<T> responseType);

        protected boolean isIgnoreLogMethod(String url, String ignoreLogMethodName) {
            if (StringUtils.isNotBlank(ignoreLogMethodName)) {
                String methodName = MwMoreUrlUtil.getUrlSuffix(url);
                Iterable<String> iterable =
                        Splitter.on(",").omitEmptyStrings().trimResults().omitEmptyStrings().split(ignoreLogMethodName);
                return Iterables.contains(iterable, methodName);
            }
            return false;
        }

        protected String getMetricsPrefixName(BaseRequest<?> baseRequest) {
            String url = baseRequest.getUrl();
            String urlMethod = MwMoreUrlUtil.getUrlSuffix(url);
            return projName + "." + urlMethod;
        }

        protected void statRestExec(String prefix, boolean isSuccess, long diffTime) {
            if (isSuccess) {
                statsDClient.increment(prefix + RestMetricsConst.REQ_EXE_SUC);
            } else {
                statsDClient.increment(prefix + RestMetricsConst.REQ_EXE_FAIL);
            }
            statsDClient.recordExecutionTime(prefix + RestMetricsConst.REQ_EXE_TIME, diffTime, 1);
        }
    }

    abstract class Async implements Template {

        public abstract <T> ListenableFuture<ResponseEntity<T>> postForEntity(BaseRequest<?> baseRequest, Class<T> responseType);
    }

}
