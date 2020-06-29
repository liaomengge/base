package cn.mwee.service.base_framework.common.extension;

import cn.mwee.base_common.utils.string.MwStringUtil;
import cn.mwee.service.base_framework.base.BaseRestResponse;
import cn.mwee.service.base_framework.base.DataResult;
import cn.mwee.service.base_framework.common.consts.ServiceConst.ResponseStatus.ErrorCodeEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by liaomengge on 16/12/3.
 */
public class JacksonWriterInterceptor implements WriterInterceptor {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        Object entry = context.getEntity();
        if (entry instanceof DataResult) {
            context.proceed();
            return;
        }

        DataResult<BaseRestResponse> result = new DataResult<>(true);
        BaseRestResponse restResponse = new BaseRestResponse() {
        };
        restResponse.setStatus(ErrorCodeEnum.RPC_ERROR.getCode());
        String rpcErrorMsg = MwStringUtil.getValue(context.getEntity());
        if (rpcErrorMsg.contains("Unrecognized field")) {
            rpcErrorMsg = "[请求参数格式错误](" + rpcErrorMsg + ")";
        }
        restResponse.setMsg(rpcErrorMsg);
        result.setData(restResponse);

        this.writeTo(context.getOutputStream(), result);
    }

    private void writeTo(OutputStream outputStream, Object obj) throws IOException {
        JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
        jsonGenerator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

        try {
            objectMapper.writeValue(outputStream, obj);
        } finally {
            if (jsonGenerator != null) {
                jsonGenerator.flush();
            }

            if (!jsonGenerator.isClosed()) {
                jsonGenerator.close();
            }
        }
    }
}
