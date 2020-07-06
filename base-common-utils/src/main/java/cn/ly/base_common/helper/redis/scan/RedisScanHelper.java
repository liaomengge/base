package cn.ly.base_common.helper.redis.scan;

import cn.ly.base_common.helper.redis.IRedisHelper;
import com.google.common.collect.Maps;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.util.SafeEncoder;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 16/10/15.
 */
public class RedisScanHelper {

    private static final int COUNT = 100;

    private final IRedisHelper jedisClusterHelper;

    public RedisScanHelper(IRedisHelper jedisClusterHelper) {
        this.jedisClusterHelper = jedisClusterHelper;
    }

    public void hscan(String key, IRedisScan redisScan) {
        ScanParams params = new ScanParams();
        params.count(COUNT);
        hscan(key, params, redisScan);
    }

    public void hscan(String key, ScanParams params, IRedisScan redisScan) {
        ScanResult<Map.Entry<byte[], byte[]>> scanResult = jedisClusterHelper.hscan(key,
                ScanParams.SCAN_POINTER_START, params);
        String nextCursor = null;
        while (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
            List<Map<String, String>> mapList = scanResult.getResult().stream().map(val -> {
                Map<String, String> resultMap = Maps.newHashMap();
                resultMap.put(SafeEncoder.encode(val.getKey()), SafeEncoder.encode(val.getValue()));
                return resultMap;
            }).collect(Collectors.toList());
            redisScan.doHandle(mapList);
            nextCursor = scanResult.getCursor();
            if (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
                scanResult = jedisClusterHelper.hscan(key, nextCursor, params);
            }
        }
    }

    /***************************************
     * 华丽的分割线(Jdk8)
     *****************************************/

    public void hscan(String key, Consumer<List<Map<String, String>>> consumer) {
        ScanParams params = new ScanParams();
        params.count(COUNT);
        hscan(key, params, consumer);
    }

    public void hscan(String key, ScanParams params, Consumer<List<Map<String, String>>> consumer) {
        ScanResult<Map.Entry<byte[], byte[]>> scanResult = jedisClusterHelper.hscan(key,
                ScanParams.SCAN_POINTER_START, params);
        String nextCursor = null;
        while (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
            List<Map<String, String>> mapList = scanResult.getResult().stream().map(val -> {
                Map<String, String> resultMap = Maps.newHashMap();
                resultMap.put(SafeEncoder.encode(val.getKey()), SafeEncoder.encode(val.getValue()));
                return resultMap;
            }).collect(Collectors.toList());
            consumer.accept(mapList);
            nextCursor = scanResult.getCursor();
            if (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
                scanResult = jedisClusterHelper.hscan(key, nextCursor, params);
            }
        }
    }

    public void sscan(String key, Consumer<List<String>> consumer) {
        ScanParams params = new ScanParams();
        params.count(COUNT);
        sscan(key, params, consumer);
    }

    public void sscan(String key, ScanParams params, Consumer<List<String>> consumer) {
        ScanResult<byte[]> scanResult = jedisClusterHelper.sscan(key, ScanParams.SCAN_POINTER_START, params);
        String nextCursor = null;
        while (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
            List<String> list = scanResult.getResult().stream().map(SafeEncoder::encode).collect(Collectors.toList());
            consumer.accept(list);
            nextCursor = scanResult.getCursor();
            if (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
                scanResult = jedisClusterHelper.sscan(key, nextCursor, params);
            }
        }
    }
}
