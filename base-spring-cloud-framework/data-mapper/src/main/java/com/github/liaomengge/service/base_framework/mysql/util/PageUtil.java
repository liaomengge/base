package com.github.liaomengge.service.base_framework.mysql.util;

import com.github.liaomengge.service.base_framework.mysql.page.MysqlPagination;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/9/20.
 */
@UtilityClass
public class PageUtil {

    public <T> PageInfo<T> select4Page(int pageNo, int pageSize, ISelect select) {
        Page<T> page = PageHelper.startPage(pageNo, pageSize);
        return page.doSelectPageInfo(select);
    }

    public <T> PageInfo<T> select4Page(MysqlPagination pagination, ISelect select) {
        return select4Page(pagination.getPageNo(), pagination.getPageSize(), select);
    }
}
