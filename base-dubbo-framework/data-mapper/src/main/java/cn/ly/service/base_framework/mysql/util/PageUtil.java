package cn.ly.service.base_framework.mysql.util;

import cn.ly.service.base_framework.mysql.page.MysqlPagination;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * Created by liaomengge on 2019/9/20.
 */
public final class PageUtil {

    private PageUtil() {
    }

    public static <T> PageInfo<T> select4Page(int pageNo, int pageSize, ISelect select) {
        Page<T> page = PageHelper.startPage(pageNo, pageSize);
        return page.doSelectPageInfo(select);
    }

    public static <T> PageInfo<T> select4Page(MysqlPagination pagination, ISelect select) {
        return select4Page(pagination.getPageNo(), pagination.getPageSize(), select);
    }
}
