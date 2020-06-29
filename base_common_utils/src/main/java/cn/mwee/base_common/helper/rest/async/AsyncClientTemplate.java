package cn.mwee.base_common.helper.rest.async;

import cn.mwee.base_common.helper.rest.Template;
import cn.mwee.base_common.helper.rest.data.BaseRequest;
import cn.mwee.base_common.utils.json.MwJsonUtil;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * Created by liaomengge on 17/3/9.
 */
@NoArgsConstructor
@Deprecated
public class AsyncClientTemplate extends Template.Async {

    @Setter
    private AsyncRestTemplate asyncRestTemplate;

    public AsyncClientTemplate(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate;
    }

    @Override
    public <T> ListenableFuture<ResponseEntity<T>> postForEntity(BaseRequest<?> baseRequest, Class<T> responseType) {
        String url = baseRequest.getUrl();
        HttpEntity httpEntity = new HttpEntity(baseRequest.getData());
        ListenableFuture<ResponseEntity<T>> listenableFuture = null;
        try {
            listenableFuture = asyncRestTemplate.postForEntity(url, httpEntity, responseType);
        } catch (Throwable e) {
            logger.error("调用服务失败, 请求参数[{}], 服务地址[{}], 异常原因 ===> [{}]", MwJsonUtil.toJson4Log(baseRequest.getData()), url, e.getMessage());
        }
        return listenableFuture;
    }
}
