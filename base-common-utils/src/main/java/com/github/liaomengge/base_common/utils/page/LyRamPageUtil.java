package com.github.liaomengge.base_common.utils.page;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2020/6/15.
 */
@UtilityClass
public class LyRamPageUtil {

    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 从第一页开始，内存分页
     *
     * @param list
     * @param pageNo
     * @param pageSize
     * @param <T>
     * @return
     */
    public <T> Pagination<T> page(List<T> list, Integer pageNo, Integer pageSize) {
        Pagination<T> pagination = new Pagination<>(pageNo, pageSize);
        if (CollectionUtils.isEmpty(list)) {
            pagination.setResult(Lists.newArrayList());
            return pagination;
        }
        if (Objects.isNull(pageNo)) {
            pageNo = DEFAULT_PAGE_NO;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageNo < 1) {
            pageNo = 1;
        }
        int from = (pageNo - 1) * pageSize;
        int to = Math.min(pageNo * pageSize, list.size());
        if (from > to) {
            from = to;
        }
        pagination.setTotalCount(list.size());
        pagination.buildTotalPage();
        pagination.setResult(list.subList(from, to));
        return pagination;
    }

    @Getter
    @Setter
    public class Pagination<T> {

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

        public Pagination(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
        }

        public void buildTotalPage() {
            int divisor = (int) (totalCount / getPageSize());
            int remainder = (int) (totalCount % getPageSize());
            setTotalPage(remainder == 0 ? divisor == 0 ? 1 : divisor : divisor + 1);
        }
    }
}
