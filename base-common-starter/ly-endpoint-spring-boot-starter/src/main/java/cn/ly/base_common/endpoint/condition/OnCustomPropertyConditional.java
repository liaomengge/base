package cn.ly.base_common.endpoint.condition;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

/**
 * Created by liaomengge on 2019/7/4.
 */
public class OnCustomPropertyConditional extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        Boolean managementBuildEnabled = environment.getProperty("management.info.build.enabled", Boolean.class);
        Boolean endpointsInfoEnabled = environment.getProperty("management.endpoint.info.enabled", Boolean.class);
        if (Objects.nonNull(managementBuildEnabled) && managementBuildEnabled.booleanValue()
                && Objects.nonNull(endpointsInfoEnabled) && endpointsInfoEnabled.booleanValue()) {
            return ConditionOutcome.match("match");
        }
        return ConditionOutcome.noMatch("noMatch");
    }
}
