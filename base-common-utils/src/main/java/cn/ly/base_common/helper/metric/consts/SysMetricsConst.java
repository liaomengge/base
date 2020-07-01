package cn.ly.base_common.helper.metric.consts;

public interface SysMetricsConst {

    /**
     * 前缀(JVM）
     */
    String PREFIX_JVM = "jvm";

    /**
     * 前缀(Thread）
     */
    String PREFIX_THREAD = "thread";

    /**
     * 前缀(Local Cache）
     */
    String PREFIX_CACHE = "cache";

    /**
     * 总内存
     */
    String JVM_MEM_TOTAL = ".mem.total";

    /**
     * 已用内存
     */
    String JVM_MEM_USED = ".mem.used";

    /**
     * 线程数
     */
    String THREAD_COUNT = ".count";

    /**
     * 本地内存数
     */
    String CACHE_MEM_COUNT = ".mem.count";

    /**
     * 前缀(ActiveMQ端）
     */
    String PREFIX_ACTIVEMQ = "activemq";

    /**
     * 前缀(RabbitMQ端）
     */
    String PREFIX_RABBITMQ = "rabbitmq";
}