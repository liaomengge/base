package cn.mwee.base_common.cache;

/**
 * Created by liaomengge on 2019/3/18.
 */
public interface Cache {

    String get(String key);

    void set(String key, String value);

    void evict(String key);
}
