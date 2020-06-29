package cn.mwee.base_common.quartz;

import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.quartz.CalendarIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING;

/**
 * Created by liaomengge on 2019/1/29.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "mwee.quartz")
public class QuartzProperties {

    private int startupDelay = 0;
    private String taskExecutorRef;
    private boolean overwriteExistingJobs = false;
    private boolean waitForJobsToCompleteOnShutdown = false;
    @NotNull
    private String basePackage;
    private List<JobInfo> jobs = Lists.newArrayList();

    @Data
    @Validated
    public static class JobInfo {
        private String subPackage;
        @NotNull
        private String className;
        @NotNull
        private String cronExpression;
        private boolean concurrent = false;
        private int misfireInstruction = MISFIRE_INSTRUCTION_DO_NOTHING;
    }

    public JobInfo findClassName(String className) {
        return this.jobs.stream()
                .filter(val -> StringUtils.equals(className, getPackageName(val)))
                .findFirst()
                .orElse(null);
    }

    public String getPackageName(JobInfo jobInfo) {
        String pkgName = this.basePackage + '.' + StringUtils.trimToEmpty(jobInfo.getClassName());
        if (StringUtils.isNotBlank(jobInfo.getSubPackage())) {
            pkgName =
                    this.basePackage + '.' + jobInfo.getSubPackage() + '.' + StringUtils.trimToEmpty(jobInfo.getClassName());
        }
        return pkgName;
    }
}
