package cn.ly.service.base_framework.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by liaomengge on 2019/3/29.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StringRestRequest extends BaseRestRequest<String> {
}
