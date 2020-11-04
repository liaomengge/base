package com.github.liaomengge.service.base_framework.common.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by liaomengge on 2020/11/2.
 */
@Data
public class ServiceApiLogInfo implements Serializable {

    private static final long serialVersionUID = -6003914785025946352L;

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
