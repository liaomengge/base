package com.github.liaomengge.base_common.cache;

import com.github.benmanes.caffeine.cache.stats.CacheStats;

/**
 * Created by liaomengge on 2019/3/18.
 */
public interface Level1Cache extends Cache {

    long size();

    void evictAll();

    CacheStats stats();
}
