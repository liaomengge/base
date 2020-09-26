package cn.ly.base_common.thread.pool.enums;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by liaomengge on 2020/8/31.
 */
@Getter
@AllArgsConstructor
public enum RejectionPolicyEnum {

    CALLER_RUNS_POLICY("CallerRunsPolicy"),
    DISCARD_POLICY("DiscardPolicy"),
    DISCARD_OLDEST_POLICY("DiscardOldestPolicy"),
    ABORT_POLICY("AbortPolicy");

    private String rejectionPolicy;

    public static RejectionPolicyEnum matchRejectionPolicy(String rejectionPolicy) {
        return Arrays.stream(RejectionPolicyEnum.values())
                .filter(val -> StringUtils.equals(val.getRejectionPolicy(), rejectionPolicy))
                .findFirst().orElse(null);
    }
}
