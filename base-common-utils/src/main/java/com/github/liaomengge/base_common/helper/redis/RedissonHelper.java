package com.github.liaomengge.base_common.helper.redis;

import com.github.liaomengge.base_common.utils.number.LyNumberUtil;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2018/10/12.
 */
public class RedissonHelper implements IRedisHelper {

    private Config config;

    @Getter
    private RedissonClient redissonClient;

    public RedissonHelper(Config config) {
        this.config = config;
        redissonClient = Redisson.create(this.config);
    }

    public RedissonHelper(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Set<String> keys(String pattern) {
        return Sets.newHashSet(redissonClient.getKeys().getKeys());
    }

    @Override
    public Long incr(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    @Override
    public Long incr(String key, long value) {
        return redissonClient.getAtomicLong(key).addAndGet(value);
    }

    @Override
    public Long decr(String key) {
        return redissonClient.getAtomicLong(key).decrementAndGet();
    }

    @Override
    public Long decr(String key, long value) {
        return redissonClient.getAtomicLong(key).addAndGet(-value);
    }

    @Override
    public void del(String key) {
        redissonClient.getKeys().delete(key);
    }

    @Override
    public void delArr(String... keys) {
        redissonClient.getKeys().delete(keys);
    }

    @Override
    public ScanResult<String> scan(String key, ScanParams params) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public void expire(String key, long milliseconds) {
        redissonClient.getBucket(key).expire(milliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public Long ttl(String key) {
        return redissonClient.getBucket(key).remainTimeToLive();
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public boolean lock(String key, String value, long expiredMillis) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public boolean unlock(String key, String value) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public void set(String key, String value) {
        redissonClient.getBucket(key).set(value);
    }

    @Override
    public void set(String key, String value, long expiredMillis) {
        redissonClient.getBucket(key).set(value, expiredMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public String get(String key) {
        RBucket<String> rBucket = redissonClient.getBucket(key);
        return rBucket.get();
    }

    @Override
    public List<String> mget(String... keys) {
        Map<String, String> map = redissonClient.getBuckets().get(keys);
        return map.values().stream().collect(Collectors.toList());
    }

    @Override
    public void hset(String key, String field, String value) {
        redissonClient.getMap(key).fastPut(field, value);
    }

    @Override
    public void hset(String key, String field, String value, long milliseconds) {
        redissonClient.getMap(key).fastPut(field, value);
        redissonClient.getBucket(key).expire(milliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public String hget(String key, String field) {
        RMap<String, String> rMap = redissonClient.getMap(key);
        return rMap.get(field);
    }

    @Override
    public Map<String, String> hgetall(String key) {
        RMap<String, String> rMap = redissonClient.getMap(key);
        return rMap.readAllMap();
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        RMap<String, String> rMap = redissonClient.getMap(key);
        return rMap.getAll(Sets.newHashSet(fields)).values().stream().collect(Collectors.toList());
    }

    @Override
    public Set<String> hkeys(String key) {
        RMap<String, String> rMap = redissonClient.getMap(key);
        return rMap.readAllKeySet();
    }

    @Override
    public List<String> hvalues(String key) {
        RMap<String, String> rMap = redissonClient.getMap(key);
        return rMap.readAllValues().stream().collect(Collectors.toList());
    }

    @Override
    public void hdel(String key, String... fields) {
        RMap<String, String> rMap = redissonClient.getMap(key);
        rMap.fastRemove(fields);
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public ScanResult<Map.Entry<byte[], byte[]>> hscan(String key, String cursor, ScanParams params) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        RList<String> rList = redissonClient.getList(key);
        return rList.readAll();
    }

    @Override
    public void lpush(String key, String value) {
        RDeque<String> rDeque = redissonClient.getDeque(key);
        rDeque.addFirst(value);
    }

    @Override
    public void rpush(String key, String value) {
        RList<String> rList = redissonClient.getList(key);
        rList.add(value);
    }

    @Override
    public String lPop(String key) {
        RQueue<String> rQueue = redissonClient.getQueue(key);
        return rQueue.poll();
    }

    @Override
    public String rpop(String key) {
        RDeque<String> rDeque = redissonClient.getDeque(key);
        return rDeque.pollLast();
    }

    @Override
    public void rpushall(String key, List<String> list) {
        RList<String> rList = redissonClient.getList(key);
        rList.addAll(list);
    }

    @Override
    public Long llen(String key) {
        RList<String> rList = redissonClient.getList(key);
        return Long.valueOf(rList.size());
    }

    @Override
    public String lindex(String key, long index) {
        RList<String> rList = redissonClient.getList(key);
        return rList.get(Math.toIntExact(index));
    }

    @Override
    public void ltrim(String key, long start, long end) {
        RList<String> rList = redissonClient.getList(key);
        rList.trim(LyNumberUtil.getIntValue(start), LyNumberUtil.getIntValue(end));
    }

    @Override
    public Set<String> smember(String key) {
        RSet<String> rSet = redissonClient.getSet(key);
        return rSet.readAll();
    }

    @Override
    public void sadd(String key, String... member) {
        RSet<String> rSet = redissonClient.getSet(key);
        rSet.addAll(Sets.newHashSet(member));
    }

    @Override
    public void srem(String key, String... member) {
        RSet<String> rSet = redissonClient.getSet(key);
        rSet.removeAll(Sets.newHashSet(member));
    }

    @Override
    public ScanResult<byte[]> sscan(String key, String cursor, ScanParams params) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public Long zadd(String key, double score, String member) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public Long zrem(String key, String... member) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        throw new UnsupportedOperationException("Redisson不兼容该返回结果集操作");
    }
}
