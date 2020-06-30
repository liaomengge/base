package cn.ly.base_common.helper.redis;

import cn.ly.base_common.utils.collection.MwArrayUtil;
import cn.ly.base_common.utils.string.MwStringUtil;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 17/11/7.
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisTemplateHelper implements IRedisHelper {

    @Getter
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    @Override
    public Long incr(String key) {
        return stringRedisTemplate.opsForValue().increment(key, 1L);
    }

    @Override
    public Long incr(String key, long value) {
        return stringRedisTemplate.opsForValue().increment(key, value);
    }

    @Override
    public Long decr(String key) {
        return stringRedisTemplate.opsForValue().increment(key, -1L);
    }

    @Override
    public Long decr(String key, long value) {
        return stringRedisTemplate.opsForValue().increment(key, -value);
    }

    @Override
    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public void delArr(String... keys) {
        stringRedisTemplate.delete(Arrays.asList(keys));
    }

    /**
     * 接口未实现
     *
     * @param key
     * @param params
     * @return
     */
    @Deprecated
    @Override
    public ScanResult<String> scan(String key, ScanParams params) {
        throw new UnsupportedOperationException("StringRedisTemplate不兼容该返回结果集操作");
    }

    @Override
    public void expire(String key, int seconds) {
        stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public Long ttl(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        return stringRedisTemplate.execute(new DefaultRedisScript(script, Object.class), keys, args);
    }

    @Override
    public boolean lock(String key, String value, long expiredSeconds) {
        throw new UnsupportedOperationException("StringRedisTemplate不兼容该返回结果集操作");
    }

    @Override
    public boolean unlock(String key, String value) {
        throw new UnsupportedOperationException("StringRedisTemplate不兼容该返回结果集操作");
    }

    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, int expiredSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, expiredSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public List<String> mget(String... keys) {
        return stringRedisTemplate.opsForValue().multiGet(Arrays.asList(keys));
    }

    @Override
    public void hset(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public void hset(String key, String field, String value, int seconds) {
        stringRedisTemplate.opsForHash().put(key, field, value);
        stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public String hget(String key, String field) {
        return MwStringUtil.getValue(stringRedisTemplate.opsForHash().get(key, field));
    }

    @Override
    public Map<String, String> hgetall(String key) {
        return stringRedisTemplate.execute((RedisCallback<Map<String, String>>) connection -> {
            Map<String, String> retMap = Maps.newHashMap();
            RedisSerializer<String> redisSerializer = stringRedisTemplate.getStringSerializer();
            byte[] keyByte = redisSerializer.serialize(key);
            Map<byte[], byte[]> retByteMap = connection.hGetAll(keyByte);
            for (Map.Entry<byte[], byte[]> entry : retByteMap.entrySet()) {
                retMap.put(redisSerializer.deserialize(entry.getKey()), redisSerializer.deserialize(entry.getValue()));
            }
            return retMap;
        });
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        Object[] fieldArr = fields;
        return Lists.transform(stringRedisTemplate.opsForHash().multiGet(key, MwArrayUtil.asList(fieldArr)),
                input -> MwStringUtil.getValue(input));
    }

    @Override
    public Set<String> hkeys(String key) {
        return Sets.newHashSet(Collections2.transform(stringRedisTemplate.opsForHash().keys(key),
                input -> MwStringUtil.getValue(input)));
    }

    @Override
    public List<String> hvalues(String key) {
        return Lists.transform(stringRedisTemplate.opsForHash().values(key), input -> MwStringUtil.getValue(input));
    }

    @Override
    public void hdel(String key, String... fields) {
        Object[] fieldArr = fields;
        stringRedisTemplate.opsForHash().delete(key, fieldArr);
    }

    /**
     * 接口未实现
     *
     * @param key
     * @param cursor
     * @return
     */
    @Deprecated
    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        throw new UnsupportedOperationException("StringRedisTemplate不兼容该返回结果集操作");
    }

    /**
     * 接口未实现
     *
     * @param key
     * @param cursor
     * @param params
     * @return
     */
    @Deprecated
    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        throw new UnsupportedOperationException("StringRedisTemplate不兼容该返回结果集操作");
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public void lpush(String key, String value) {
        stringRedisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public void rpush(String key, String value) {
        stringRedisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public String lPop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    @Override
    public String rpop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    @Override
    public void rpushall(String key, List<String> list) {
        stringRedisTemplate.opsForList().rightPushAll(key, list);
    }

    @Override
    public Long llen(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    @Override
    public String lindex(String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    @Override
    public void ltrim(String key, long start, long end) {
        stringRedisTemplate.opsForList().trim(key, start, end);
    }

    @Override
    public Set<String> smember(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    @Override
    public void sadd(String key, String... member) {
        stringRedisTemplate.opsForSet().add(key, member);
    }

    @Override
    public void srem(String key, String... member) {
        Object[] memberArr = member;
        stringRedisTemplate.opsForSet().remove(key, memberArr);
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        throw new UnsupportedOperationException("StringRedisTemplate不兼容该返回结果集操作");
    }

    @Override
    public Long zadd(String key, double score, String member) {
        return stringRedisTemplate.opsForZSet().add(key, Sets.newHashSet(new DefaultTypedTuple(member, score)));
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        Set<ZSetOperations.TypedTuple<String>> set = stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
        return set.stream().map(val -> new Tuple(val.getValue(), val.getScore())).collect(Collectors.toSet());
    }

    @Override
    public Long zrem(String key, String... member) {
        Object[] memberArr = member;
        return stringRedisTemplate.opsForZSet().remove(key, memberArr);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        Set<ZSetOperations.TypedTuple<String>> set = stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min,
                max);
        return set.stream().map(val -> new Tuple(val.getValue(), val.getScore())).collect(Collectors.toSet());
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        Set<ZSetOperations.TypedTuple<String>> set = stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min,
                max, offset, count);
        return set.stream().map(val -> new Tuple(val.getValue(), val.getScore())).collect(Collectors.toSet());
    }
}
