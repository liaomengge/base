package cn.ly.base_common.mq.consts;

/**
 * Created by liaomengge on 2016/8/30.
 */
public final class MetricsConst {

    /**
     * 发送到接受处理时间
     */
    public static final String SEND_2_RECEIVE_EXEC_TIME = ".send.to.receive.exec.time";

    /**
     * 接受到消费处理时间
     */
    public static final String RECEIVE_2_HANDLE_EXEC_TIME = ".receive.to.handle.exec.time";

    /**
     * Ack处理异常
     */
    public static final String EXEC_ACK_EXCEPTION = ".exec.ack.exception";

    /**
     * 执行处理异常
     */
    public static final String EXEC_EXCEPTION = ".exec.exception";

    /**
     * 入队个数
     */
    public static final String ENQUEUE_COUNT = ".enqueue.count";

    /**
     * 出队个数
     */
    public static final String DEQUEUE_COUNT = ".dequeue.count";
}