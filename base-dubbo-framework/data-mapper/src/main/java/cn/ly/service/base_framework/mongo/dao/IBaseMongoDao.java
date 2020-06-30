package cn.ly.service.base_framework.mongo.dao;

import cn.ly.service.base_framework.mongo.domain.BaseMongoDoc;
import cn.ly.service.base_framework.mongo.page.MongoPagination;
import com.mongodb.WriteResult;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by liaomengge on 17/3/2.
 */
public interface IBaseMongoDao<T extends BaseMongoDoc> {

    /******************************插入******************************/

    T insert(T entity);

    void batchInsert(List<T> list);

    void batchInsert(List<T> list, int batchSize);

    /******************************查找******************************/

    T findByObjId(String objId);

    List<T> find(Query query);

    T findOne(Query query);

    MongoPagination<T> findPage(Query query, MongoPagination<T> page);

    List<T> findAll();

    /******************************更新******************************/

    WriteResult updateFirst(Query query, Update update);

    WriteResult updateFirst(Query query, @NonNull T entity, String... excludePropertyName);

    WriteResult updateFirst(Query query, @NonNull Map<String, Object> map, String... excludePropertyName);

    WriteResult updateMulti(Query query, Update update);

    WriteResult updateMulti(Query query, @NonNull T entity, String... excludePropertyName);

    WriteResult updateMulti(Query query, @NonNull Map<String, Object> map, String... excludePropertyName);

    T updateOne(Query query, Update update);

    /******************************删除******************************/

    void remove(Query query);

    /******************************统计******************************/

    long count(Query query);
}
