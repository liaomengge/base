package com.github.liaomengge.base_common.dayu.custom.lua;

import com.github.liaomengge.base_common.utils.io.LyMoreIOUtil;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * Created by liaomengge on 2019/6/26.
 */
public class LuaUtil {

    public static final String CIRCUIT_COUNTER = "circuit_counter";

    @Getter
    private static Map<String, String> luaMap = Maps.newHashMap();

    //init 配置文件
    static {
        String script = LyMoreIOUtil.loadScript("lua/circuit_counter.lua");
        luaMap.put(CIRCUIT_COUNTER, script);
    }
}
