package cn.ly.service.base_framework.mongo.page;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by liaomengge on 17/3/2.
 */
@Getter
@Setter
public class MongoPagination<T> implements Serializable {

    private static final long serialVersionUID = 2105757140408812247L;

    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 当前页数
     */
    private int pageNo;

    /**
     * 每页显示个数
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 总记录数
     */
    private long totalCount;

    /**
     * 结果列表
     */
    private List<T> result;

    public MongoPagination() {
        pageNo = DEFAULT_PAGE_NO;
        pageSize = DEFAULT_PAGE_SIZE;
    }

    public MongoPagination(int pageNo, int pageSize) {
        this.pageNo = pageNo <= 0 ? DEFAULT_PAGE_NO : pageNo;
        this.pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    /**
     * 设置结果及总页数
     *
     * @param result
     */
    public void build(List<T> result) {
        setResult(result);
        long count = getTotalCount();
        int divisor = (int) (count / getPageSize());
        int remainder = (int) (count % getPageSize());
        setTotalPage(remainder == 0 ? divisor == 0 ? 1 : divisor : divisor + 1);
    }

    /**
     * 跳过多少条记录
     *
     * @return
     */
    public int getSkip() {
        return (pageNo - 1) * pageSize;
    }

}
