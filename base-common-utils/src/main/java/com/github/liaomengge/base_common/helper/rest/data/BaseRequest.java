package com.github.liaomengge.base_common.helper.rest.data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by liaomengge on 17/3/9.
 */
@Data
@AllArgsConstructor
public class BaseRequest<T> {

    private String url;
    private T data;

    public BaseRequest(String url) {
        this.url = url;
    }
}
