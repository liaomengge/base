package cn.mwee.base_common.cache;

/**
 * Created by liaomengge on 2019/3/18.
 */
public interface Level2Cache extends Cache {

    void set(String key, String value, int expiredSeconds);
}
