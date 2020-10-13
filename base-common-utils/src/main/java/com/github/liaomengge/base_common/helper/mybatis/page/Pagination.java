package com.github.liaomengge.base_common.helper.mybatis.page;

import lombok.Getter;

/**
 * Created by liaomengge on 17/7/26.
 */
public class Pagination {

    public static final int DEFAULT_PAGE_NO = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 当前页数
     * (注：传的是页码数, 但是实际转换成了offset) - 使用时, 一定要注意
     */
    @Getter
    private int pageNo;

    /**
     * 每页显示个数
     */
    @Getter
    private int pageSize;

    public Pagination() {
    }

    public Pagination(int pageNo, int pageSize) {
        this.setPageSize(pageSize);
        this.setPageNo(pageNo);
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo <= 0 ? DEFAULT_PAGE_NO : (pageNo - 1) * this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
