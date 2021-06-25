package com.github.liaomengge.base_common.helper.retrofit;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.github.liaomengge.base_common.support.exception.CommunicationException;
import com.github.liaomengge.base_common.utils.error.LyExceptionUtil;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.retry.support.RetryTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by liaomengge on 2019/3/6.
 */
@Slf4j
@AllArgsConstructor
public class RetrofitHelper {
    
    private final RetryTemplate retryTemplate;

    public <T> T execute(Call<T> call) {
        return execute(call, true);
    }

    public <T> T execute(Call<T> call, boolean retry) {
        try {
            Response<T> response = retry ? retryTemplate.execute(context -> call.clone().execute()) : call.execute();
            return extract(response);
        } catch (Throwable t) {
            this.handleThrowable(t);
        }
        return null;
    }

    private <T> T extract(Response<T> response) {
        if (response.isSuccessful()) {
            return response.body();
        }
        throw new CommunicationException(response.message());
    }

    /************************************************华丽的分割线*******************************************************/

    public String execute2(Call<ResponseBody> call) {
        return execute2(call, true);
    }

    public String execute2(Call<ResponseBody> call, boolean retry) {
        try {
            Response<ResponseBody> response = retry ? retryTemplate.execute(context -> call.clone().execute()) :
                    call.execute();
            return extract2(response);
        } catch (Throwable t) {
            this.handleThrowable(t);
        }
        return null;
    }

    public <T> T execute2(Call<ResponseBody> call, Class<T> respClass) {
        return execute2(call, respClass, true);
    }

    public <T> T execute2(Call<ResponseBody> call, Class<T> respClass, boolean retry) {
        try {
            Response<ResponseBody> response = retry ? retryTemplate.execute(context -> call.clone().execute()) :
                    call.execute();
            return extract2(response, respClass);
        } catch (Throwable t) {
            this.handleThrowable(t);
        }
        return null;
    }

    private String extract2(Response<ResponseBody> response) throws IOException {
        if (response.isSuccessful()) {
            return response.body().string();
        }
        return response.errorBody().string();
    }

    private <T> T extract2(Response<ResponseBody> response, Class<T> clz) throws IOException {
        return LyJacksonUtil.fromJson(extract2(response), clz);
    }

    /************************************************华丽的分割线*******************************************************/

    public <T> void enqueue(Call<T> call, Callback<T> callback) {
        call.enqueue(callback);
    }

    private void handleThrowable(Throwable t) {
        if (t instanceof BlockException || LyExceptionUtil.unwrap(t) instanceof BlockException) {
            BlockException e = (BlockException) LyExceptionUtil.unwrap(t);
            Optional.ofNullable(e).map(BlockException::getRule).map(AbstractRule::getResource).ifPresent(val -> log.warn("Retrofit Block处理异常 ===> ", t));
        } else {
            log.warn("处理异常 ===> ", t);
        }
    }
}
