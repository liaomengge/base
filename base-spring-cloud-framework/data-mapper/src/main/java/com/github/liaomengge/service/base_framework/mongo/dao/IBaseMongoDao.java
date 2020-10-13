package com.github.liaomengge.service.base_framework.mongo.dao;

import com.github.liaomengge.service.base_framework.mongo.domain.BaseMongoDoc;
import com.github.liaomengge.service.base_framework.mongo.page.MongoPagination;

import com.mongodb.client.result.UpdateResult;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import lombok.NonNull;

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

    UpdateResult updateFirst(Query query, Update update);

    UpdateResult updateFirst(Query query, @NonNull T entity, String... excludePropertyName);

    UpdateResult updateFirst(Query query, @NonNull Map<String, Object> map, String... excludePropertyName);

    UpdateResult updateMulti(Query query, Update update);

    UpdateResult updateMulti(Query query, @NonNull T entity, String... excludePropertyName);

    UpdateResult updateMulti(Query query, @NonNull Map<String, Object> map, String... excludePropertyName);

    T updateOne(Query query, Update update);

    /******************************删除******************************/

    void remove(Query query);

    /******************************统计******************************/

    long count(Query query);
}
