package cn.ly.base_common.helper.redis;

import cn.ly.base_common.utils.collection.LyArrayUtil;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.util.SafeEncoder;

/**
 * Created by liaomengge on 17/11/7.
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JedisClusterHelper implements IRedisHelper {

    @Getter
    private JedisCluster jedisCluster;

    //****************************************通用*******************************************//

    /**
     * 由于JedisCluster没有提供对keys命令的封装,只能自己实现,`慎用`
     *
     * @param pattern
     * @return
     */
    @Override
    public Set<String> keys(String pattern) {
        Set<String> keys = Sets.newHashSet();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for (String key : clusterNodes.keySet()) {
            JedisPool jedisPool = clusterNodes.get(key);
            Jedis connection = jedisPool.getResource();
            try {
                keys.addAll(connection.keys(pattern));
            } catch (Exception e) {
                log.error("get keys failed", e);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }
        return keys;
    }

    @Override
    public Long incr(String key) {
        return jedisCluster.incr(key);
    }

    @Override
    public Long incr(String key, long value) {
        return jedisCluster.incrBy(key, value);
    }

    @Override
    public Long decr(String key) {
        return jedisCluster.decr(key);
    }

    @Override
    public Long decr(String key, long value) {
        return jedisCluster.decrBy(key, value);
    }

    @Override
    public void del(String key) {
        jedisCluster.del(key);
    }

    @Override
    public void delArr(String... keys) {
        jedisCluster.del(keys);
    }

    @Override
    public ScanResult<String> scan(String key, ScanParams params) {
        return jedisCluster.scan(key, params);
    }

    @Override
    public void expire(String key, int seconds) {
        jedisCluster.expire(key, seconds);
    }

    @Override
    public Long ttl(String key) {
        return jedisCluster.ttl(key);
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        return jedisCluster.eval(script, keys, args);
    }

    @Override
    public boolean lock(String key, String value, long expiredSeconds) {
        String result = jedisCluster.set(LOCK_PREFIX + key, value, SetParams.setParams().nx().ex((int) expiredSeconds));
        return LOCK_SUCCESS.equals(result);
    }

    @Override
    public boolean unlock(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return " +
                "0 end";
        Object result = jedisCluster.eval(script, Collections.singletonList(LOCK_PREFIX + key),
                Collections.singletonList(value));
        return RELEASE_SUCCESS.equals(result);
    }

    //****************************************string*******************************************//

    @Override
    public void set(String key, String value) {
        jedisCluster.set(key, value);
    }

    @Override
    public void set(String key, String value, int expiredSeconds) {
        jedisCluster.setex(key, expiredSeconds, value);
    }

    @Override
    public String get(String key) {
        return jedisCluster.get(key);
    }

    @Override
    public List<String> mget(String... keys) {
        return jedisCluster.mget(keys);
    }

    //****************************************hash*******************************************//

    @Override
    public void hset(String key, String field, String value) {
        jedisCluster.hset(key, field, value);
    }

    @Override
    public void hset(String key, String field, String value, int seconds) {
        jedisCluster.hset(key, field, value);
        jedisCluster.expire(key, seconds);
    }

    @Override
    public String hget(String key, String field) {
        return jedisCluster.hget(key, field);
    }

    @Override
    public Map<String, String> hgetall(String key) {
        return jedisCluster.hgetAll(key);
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return jedisCluster.hmget(key, fields);
    }

    @Override

    public Set<String> hkeys(String key) {
        return jedisCluster.hkeys(key);
    }

    @Override
    public List<String> hvalues(String key) {
        return jedisCluster.hvals(key);
    }

    @Override
    public void hdel(String key, String... fields) {
        jedisCluster.hdel(key, fields);
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        return jedisCluster.hscan(key, cursor);
    }

    @Override
    public ScanResult<Map.Entry<byte[], byte[]>> hscan(String key, String cursor, ScanParams params) {
        return jedisCluster.hscan(SafeEncoder.encode(key), SafeEncoder.encode(cursor), params);
    }

    //****************************************list*******************************************//

    @Override
    public List<String> lrange(String key, long start, long end) {
        return jedisCluster.lrange(key, start, end);
    }

    @Override
    public void lpush(String key, String value) {
        jedisCluster.lpush(key, value);
    }

    @Override
    public void rpush(String key, String value) {
        jedisCluster.rpush(key, value);
    }

    @Override
    public String lPop(String key) {
        return jedisCluster.lpop(key);
    }

    @Override
    public String rpop(String key) {
        return jedisCluster.rpop(key);
    }

    @Override
    public void rpushall(String key, List<String> list) {
        jedisCluster.rpush(key, LyArrayUtil.toArray(list, String.class));
    }

    @Override
    public Long llen(String key) {
        return jedisCluster.llen(key);
    }

    @Override
    public String lindex(String key, long index) {
        return jedisCluster.lindex(key, index);
    }

    @Override
    public void ltrim(String key, long start, long end) {
        jedisCluster.ltrim(key, start, end);
    }


    //****************************************set*******************************************//

    @Override
    public Set<String> smember(String key) {
        return jedisCluster.smembers(key);
    }

    @Override
    public void sadd(String key, String... member) {
        jedisCluster.sadd(key, member);
    }


    @Override
    public void srem(String key, String... member) {
        jedisCluster.srem(key, member);
    }

    @Override
    public ScanResult<byte[]> sscan(String key, String cursor, ScanParams params) {
        return jedisCluster.sscan(SafeEncoder.encode(key), SafeEncoder.encode(cursor), params);
    }

    //****************************************sort set*******************************************//

    @Override
    public Long zadd(String key, double score, String member) {
        return jedisCluster.zadd(key, score, member);
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        return jedisCluster.zrange(key, start, end);
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        return jedisCluster.zrangeWithScores(key, start, end);
    }

    @Override
    public Long zrem(String key, String... member) {
        return jedisCluster.zrem(key, member);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return jedisCluster.zrangeByScore(key, min, max);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return jedisCluster.zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return jedisCluster.zrangeByScoreWithScores(key, min, max);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        return jedisCluster.zrangeByScoreWithScores(key, min, max, offset, count);
    }

}
