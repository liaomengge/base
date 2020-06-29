package cn.mwee.base_common.helper.redis.scan;

import cn.mwee.base_common.helper.redis.IRedisHelper;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
        ScanResult<Map.Entry<String, String>> scanResult = jedisClusterHelper.hscan(key, ScanParams.SCAN_POINTER_START, params);
        String nextCursor = null;
        while (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
            redisScan.doHandle(scanResult.getResult());
            nextCursor = scanResult.getStringCursor();
            if (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
                scanResult = jedisClusterHelper.hscan(key, nextCursor, params);
            }
        }
    }

    /***************************************
     * 华丽的分割线(Jdk8)
     *****************************************/

    public void hscan(String key, Consumer<List<Map.Entry<String, String>>> consumer) {
        ScanParams params = new ScanParams();
        params.count(COUNT);
        hscan(key, params, consumer);
    }

    public void hscan(String key, ScanParams params, Consumer<List<Map.Entry<String, String>>> consumer) {
        ScanResult<Map.Entry<String, String>> scanResult = jedisClusterHelper.hscan(key, ScanParams.SCAN_POINTER_START, params);
        String nextCursor = null;
        while (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
            consumer.accept(scanResult.getResult());
            nextCursor = scanResult.getStringCursor();
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
        ScanResult<String> scanResult = jedisClusterHelper.sscan(key, ScanParams.SCAN_POINTER_START, params);
        String nextCursor = null;
        while (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
            consumer.accept(scanResult.getResult());
            nextCursor = scanResult.getStringCursor();
            if (!ScanParams.SCAN_POINTER_START.equals(nextCursor)) {
                scanResult = jedisClusterHelper.sscan(key, nextCursor, params);
            }
        }
    }
}
