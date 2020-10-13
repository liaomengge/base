package com.github.liaomengge.base_common.cache.domain;

import com.github.liaomengge.base_common.cache.enums.NotifyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by liaomengge on 2019/7/1.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheDomain {

    private String region;
    private String key;
    private String value;
    private NotifyTypeEnum notifyTypeEnum;
}
