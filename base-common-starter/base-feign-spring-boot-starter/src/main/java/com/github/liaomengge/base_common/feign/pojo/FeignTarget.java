package com.github.liaomengge.base_common.feign.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by liaomengge on 2020/12/11.
 */
@Data
@Builder
public class FeignTarget {

    private Class<?> type;
    private String name;
    private String url;
    private String targetUrl;
    private String contextId;
    private String path;
}
