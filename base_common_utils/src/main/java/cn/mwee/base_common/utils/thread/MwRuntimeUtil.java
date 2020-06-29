package cn.mwee.base_common.utils.thread;

import cn.mwee.base_common.utils.number.MwMoreNumberUtil;
import com.google.common.base.Joiner;
import lombok.experimental.UtilityClass;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;

/**
 * Created by liaomengge on 2019/6/3.
 */
@UtilityClass
public class MwRuntimeUtil {

    private static volatile int pId = -1;
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 获得当前进程的PID
     * <p>
     * 当失败时返回-1
     *
     * @return pid
     */
    public static int getPId() {
        if (pId > 0) {
            return pId;
        }
        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int index = jvmName.indexOf("@");
        if (index > 0) {
            pId = MwMoreNumberUtil.toInt(jvmName.substring(0, index), -1);
            return pId;
        }
        return -1;
    }

    /**
     * 返回应用启动到现在的时间
     *
     * @return {Duration}
     */
    public static Duration getUpTime() {
        long upTime = ManagementFactory.getRuntimeMXBean().getUptime();
        return Duration.ofMillis(upTime);
    }

    /**
     * 返回输入的JVM参数列表
     *
     * @return jvm参数
     */
    public static String getJvmArguments() {
        List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        return Joiner.on(" ").join(vmArguments);
    }

    /**
     * 获取CPU核数
     *
     * @return cpu count
     */
    public static int getCpuNum() {
        return CPU_NUM;
    }
}
