package cn.mwee.base_common.cache.channel;

import java.util.function.Consumer;

/**
 * Created by liaomengge on 2019/3/20.
 */
public interface Channel {

    void doPubChannel(String msg);

    void doSubChannel(Consumer<String> consumer);

    String getChannelName();
}
