//package cn.ly.base_common.metric.metrics.thread;
//
//import com.google.common.collect.Lists;
//import org.springframework.boot.actuate.metrics.Metric;
//
//import java.lang.management.ManagementFactory;
//import java.lang.management.ThreadInfo;
//import java.lang.management.ThreadMXBean;
//import java.util.Collection;
//import java.util.List;
//import java.util.Objects;
//
///**
// * Created by liaomengge on 2019/7/25.
// */
//public class ThreadStatePublicMetrics extends AbstractPublicMetrics {
//
//    @Override
//    public Collection<Metric<?>> metrics() {
//        List<Metric<?>> metrics = Lists.newArrayList();
//        int newThreadCount = 0;
//        int runnableThreadCount = 0;
//        int blockedThreadCount = 0;
//        int waitThreadCount = 0;
//        int timedWaitThreadCount = 0;
//        int terminatedThreadCount = 0;
//        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
//        ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadBean.getAllThreadIds());
//        if (Objects.nonNull(threadInfos)) {
//            for (ThreadInfo threadInfo : threadInfos) {
//                if (Objects.nonNull(threadInfo)) {
//                    switch (threadInfo.getThreadState()) {
//                        case NEW:
//                            newThreadCount++;
//                            break;
//                        case RUNNABLE:
//                            runnableThreadCount++;
//                            break;
//                        case BLOCKED:
//                            blockedThreadCount++;
//                            break;
//                        case WAITING:
//                            waitThreadCount++;
//                            break;
//                        case TIMED_WAITING:
//                            timedWaitThreadCount++;
//                            break;
//                        case TERMINATED:
//                            terminatedThreadCount++;
//                            break;
//                        default:
//                            break;
//                    }
//                } else {
//                    terminatedThreadCount++;
//                }
//            }
//        }
//        metrics.add(new Metric<>("threads.state.new", newThreadCount));
//        metrics.add(new Metric<>("threads.state.runnable", runnableThreadCount));
//        metrics.add(new Metric<>("threads.state.blocked", blockedThreadCount));
//        metrics.add(new Metric<>("threads.state.waiting", waitThreadCount));
//        metrics.add(new Metric<>("threads.state.timed_waiting", timedWaitThreadCount));
//        metrics.add(new Metric<>("threads.state.terminated", terminatedThreadCount));
//        return metrics;
//    }
//}
