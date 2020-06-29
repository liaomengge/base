package cn.mwee.service.base_framework.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by liaomengge on 2018/11/23.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StringRestResponse extends BaseRestResponse<String> {

    public StringRestResponse(String errNo, String errMsg) {
        super(errNo, errMsg);
    }

    public StringRestResponse(String errNo, String errMsg, String errException) {
        super(errNo, errMsg, errException);
    }
}
