package cn.ly.base_common.helper.redis;

import cn.ly.base_common.utils.log4j2.LyLogger;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

/**
 * Created by liaomengge on 4/2/16.
 */
public interface IRedisHelper {

    Logger log = LyLogger.getInstance(IRedisHelper.class);

    String LOCK_PREFIX = "lock:";
    String SET_IF_NOT_EXIST = "NX";
    String SET_WITH_EXPIRE_TIME = "EX";
    String LOCK_SUCCESS = "OK";
    Long RELEASE_SUCCESS = 1L;

    //****************************************通用*******************************************//
    Set<String> keys(String pattern);

    Long incr(String key);

    Long incr(String key, long value);

    Long decr(String key);

    Long decr(String key, long value);

    void del(String key);

    void delArr(String... keys);

    ScanResult<String> scan(String key, ScanParams params);

    void expire(String key, int seconds);

    Long ttl(String key);

    Object eval(String script, List<String> keys, List<String> args);

    boolean lock(String key, String value, long expiredSeconds);

    boolean unlock(String key, String value);

    //****************************************string*******************************************//


    void set(String key, String value);

    void set(String key, String value, int expiredSeconds);

    String get(String key);

    List<String> mget(String... keys);


    //****************************************hash*******************************************//


    void hset(String key, String field, String value);

    void hset(String key, String field, String value, int seconds);

    String hget(String key, String field);

    Map<String, String> hgetall(String key);

    List<String> hmget(String key, String... fields);

    Set<String> hkeys(String key);

    List<String> hvalues(String key);

    void hdel(String key, String... fields);

    ScanResult<Map.Entry<String, String>> hscan(String key, String cursor);

    ScanResult<Map.Entry<byte[], byte[]>> hscan(String key, String cursor, ScanParams params);

    //****************************************list*******************************************//


    List<String> lrange(String key, long start, long end);

    void lpush(String key, String value);

    void rpush(String key, String value);

    String lPop(String key);

    String rpop(String key);

    void rpushall(String key, List<String> list);

    Long llen(String key);

    String lindex(String key, long index);

    void ltrim(String key, long start, long end);


    //****************************************set*******************************************//


    Set<String> smember(String key);

    void sadd(String key, String... member);

    void srem(String key, String... member);

    ScanResult<byte[]> sscan(String key, String cursor, ScanParams params);

    //****************************************sort set*******************************************//

    Long zadd(String key, double score, String member);

    Set<String> zrange(String key, long start, long end);

    Set<Tuple> zrangeWithScores(String key, long start, long end);

    Long zrem(String key, String... member);

    Set<String> zrangeByScore(String key, double min, double max);

    Set<String> zrangeByScore(String key, double min, double max, int offset, int count);

    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max);

    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count);
}
