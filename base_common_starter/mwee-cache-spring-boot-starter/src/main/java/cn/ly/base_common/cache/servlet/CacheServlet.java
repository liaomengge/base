package cn.ly.base_common.cache.servlet;

import cn.mwee.base_common.cache.CachePoolHelper;
import cn.mwee.base_common.utils.web.MwWebUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static cn.ly.base_common.cache.servlet.enums.EvictTypeEnum.*;

/**
 * Created by liaomengge on 2019/7/2.
 */
public class CacheServlet extends HttpServlet {

    @Autowired
    private CachePoolHelper cachePoolHelper;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RespBody respBody = new RespBody();
        Map<String, String> paramMap = MwWebUtil.getRequestStringParams(req);
        String key = MapUtils.getString(paramMap, "key");
        if (StringUtils.isBlank(key)) {
            MwWebUtil.renderJson(resp, respBody.setSuccess(false).setMsg("[evict]键值key[" + key + "]不存在"));
            return;
        }
        String type = MapUtils.getString(paramMap, "type");
        if (LEVEL1.name().equalsIgnoreCase(type)) {
            String region = MapUtils.getString(paramMap, "region");
            if (StringUtils.isNotBlank(region)) {
                cachePoolHelper.evictLevel1(region, key);
            } else {
                cachePoolHelper.evictLevel1(key);
            }
            MwWebUtil.renderJson(resp, respBody.setMsg("[evict]一级缓存成功"));
        } else if (LEVEL2.name().equalsIgnoreCase(type)) {
            cachePoolHelper.evictLevel2(key);
            MwWebUtil.renderJson(resp, respBody.setMsg("[evict]二级缓存成功"));
        } else if (ALL.name().equalsIgnoreCase(type)) {
            String region = MapUtils.getString(paramMap, "region");
            if (StringUtils.isNotBlank(region)) {
                cachePoolHelper.evict(region, key);
            } else {
                cachePoolHelper.evict(key);
            }
            MwWebUtil.renderJson(resp, respBody.setMsg("[evict]一二级缓存成功"));
        } else {
            MwWebUtil.renderJson(resp, respBody.setSuccess(false).setMsg("[evict]类型type[" + type + "]不合法"));
        }
    }

    @Data
    @Accessors(chain = true)
    private static class RespBody {
        private boolean success;
        private String msg;

        public RespBody() {
            this.success = true;
        }
    }
}
