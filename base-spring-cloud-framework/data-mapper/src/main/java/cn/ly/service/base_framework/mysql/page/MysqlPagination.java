package cn.ly.service.base_framework.mysql.page;

import java.io.Serializable;

import lombok.Getter;

/**
 * Created by liaomengge on 2019/9/20.
 */
public class MysqlPagination implements Serializable {

    private static final long serialVersionUID = -6641195250105751960L;

    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 当前页数
     */
    @Getter
    private int pageNo;

    /**
     * 每页显示个数
     */
    @Getter
    private int pageSize;

    public MysqlPagination() {
        pageNo = DEFAULT_PAGE_NO;
        pageSize = DEFAULT_PAGE_SIZE;
    }

    public MysqlPagination(int pageNo, int pageSize) {
        setPageNo(pageNo);
        setPageSize(pageSize);
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo <= 0 ? DEFAULT_PAGE_NO : pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
