package cn.ly.base_common.base.mybatis.batch;

import cn.ly.base_common.helper.mybatis.batch.AbstractGeneralService;
import cn.ly.base_common.helper.mybatis.extension.MapResultHandler;
import lombok.AllArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by liaomengge on 2019/4/23.
 */
@AllArgsConstructor
public class BatchGeneralService extends AbstractGeneralService {

    private SqlSessionFactory sqlSessionFactory;

    public <T> void batchInsertEntry(Class<? extends Mapper<T>> clazz, List<T> list) {
        super.batchInsertEntry(sqlSessionFactory, clazz, list);
    }

    public <T> void batchInsertEntry(Class<? extends Mapper<T>> clazz, List<T> list, int size) {
        super.batchInsertEntry(sqlSessionFactory, clazz, list, size);
    }

    public <T> void batchInsertEntry(String statement, List<T> list) {
        super.batchInsertEntry(sqlSessionFactory, statement, list);
    }

    public <T> void batchInsertEntry(String statement, List<T> list, int size) {
        super.batchInsertEntry(sqlSessionFactory, statement, list, size);
    }

    public <T> void batchUpdateEntry(Class<? extends Mapper<T>> clazz, List<T> list) {
        super.batchUpdateEntry(sqlSessionFactory, clazz, list);
    }

    public <T> void batchUpdateEntry(Class<? extends Mapper<T>> clazz, List<T> list, int size) {
        super.batchUpdateEntry(sqlSessionFactory, clazz, list, size);
    }

    public <T> void batchUpdateEntry(String statement, List<T> list) {
        super.batchUpdateEntry(sqlSessionFactory, statement, list);
    }

    public <T> void batchUpdateEntry(String statement, List<T> list, int size) {
        super.batchUpdateEntry(sqlSessionFactory, statement, list, size);
    }

    public <T> void batchDelEntry(Class<? extends Mapper<T>> clazz, List<T> list) {
        super.batchDelEntry(sqlSessionFactory, clazz, list);
    }

    public <T> void batchDelEntry(Class<? extends Mapper<T>> clazz, List<T> list, int size) {
        super.batchDelEntry(sqlSessionFactory, clazz, list, size);
    }

    public <T> void batchDelEntry(String statement, List<T> list) {
        super.batchDelEntry(sqlSessionFactory, statement, list);
    }

    public <T> void batchDelEntry(String statement, List<T> list, int size) {
        super.batchDelEntry(sqlSessionFactory, statement, list, size);
    }

    public <T extends Map<?, ?>, K, V> Map<K, V> queryForMap(String statement
            , Object parameter, MapResultHandler<T, K, V> handler) {
        return super.queryForMap(sqlSessionFactory, statement, parameter, handler);
    }

    public <T extends Map<?, ?>, K, V> Map<K, V> queryForMap(String statement
            , MapResultHandler<T, K, V> handler) {
        return super.queryForMap(sqlSessionFactory, statement, handler);
    }
}
