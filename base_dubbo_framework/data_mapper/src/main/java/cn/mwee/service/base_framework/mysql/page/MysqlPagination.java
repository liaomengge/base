package cn.mwee.service.base_framework.mysql.page;

import lombok.Getter;

import java.io.Serializable;

/**
 * Created by liaomengge on 2019/9/20.
 */
public class MysqlPagination implements Serializable {

    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 当前页数
     */
    @Getter
    private int pageNo;//页码从0开始

    /**
     * 每页显示个数
     */
    @Getter
    private int pageSize;

    public MysqlPagination() {
        pageNo = 1;
        pageSize = DEFAULT_PAGE_SIZE;
    }

    public MysqlPagination(int pageNo, int pageSize) {
        setPageNo(pageNo);
        setPageSize(pageSize);
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo <= 0 ? 1 : pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
