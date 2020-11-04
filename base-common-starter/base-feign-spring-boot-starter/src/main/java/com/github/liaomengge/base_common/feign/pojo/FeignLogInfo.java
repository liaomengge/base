package com.github.liaomengge.base_common.feign.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by liaomengge on 2020/11/2.
 */
@Data
public class FeignLogInfo implements Serializable {
    private static final long serialVersionUID = 6865716526494531046L;

    private String url;
    private String httpMethod;
    private String classMethod;
    private Object headerParams;
    private Object queryParams;
    private Object requestBody = "NULL";
    private Object responseBody = "NULL";
    private String exceptionStackTrace;
    private Long elapsedTime;

    public void setRequestBody(Object requestBody) {
        if (Objects.nonNull(requestBody)) {
            this.requestBody = requestBody;
        }
    }

    public void setResponseBody(Object responseBody) {
        if (Objects.nonNull(responseBody)) {
            this.responseBody = responseBody;
        }
    }
}
