package cn.ly.service.base_framework.mongo.service;

import cn.ly.service.base_framework.mongo.dao.impl.BaseMongoDao;
import cn.ly.service.base_framework.mongo.domain.BaseMongoDoc;
import cn.ly.service.base_framework.mongo.page.MongoPagination;
import com.mongodb.WriteResult;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by liaomengge on 17/3/2.
 */
public abstract class BaseMongoService<T extends BaseMongoDoc> {

    @Autowired
    private BaseMongoDao<T> baseMongoDao;

    public T insert(T entity) {
        return baseMongoDao.insert(entity);
    }

    public void batchInsert(List<T> list) {
        baseMongoDao.batchInsert(list);
    }

    public void batchInsert(List<T> list, int batchSize) {
        List<T> subList;
        while (!list.isEmpty()) {
            subList = list.subList(0, Math.min(batchSize, list.size()));
            baseMongoDao.batchInsert(subList);
            subList.clear();
        }
    }

    public T findByObjectId(String objectId) {
        return baseMongoDao.findByObjId(objectId);
    }

    public List<T> find(Query query) {
        return baseMongoDao.find(query);
    }

    public T findOne(Query query) {
        return baseMongoDao.findOne(query);
    }

    public MongoPagination<T> findPage(MongoPagination<T> page, Query query) {
        return baseMongoDao.findPage(query, page);
    }

    public List<T> findAll() {
        return baseMongoDao.findAll();
    }

    public WriteResult updateFirst(Query query, Update update) {
        return baseMongoDao.updateFirst(query, update);
    }

    public WriteResult updateFirst(Query query, @NonNull T entity, String... excludePropertyName) {
        return baseMongoDao.updateFirst(query, entity, excludePropertyName);
    }

    public WriteResult updateFirst(Query query, @NonNull Map<String, Object> map, String... excludePropertyName) {
        return baseMongoDao.updateFirst(query, map, excludePropertyName);
    }

    public WriteResult updateMulti(Query query, Update update) {
        return baseMongoDao.updateMulti(query, update);
    }

    public WriteResult updateMulti(Query query, @NonNull T entity, String... excludePropertyName) {
        return baseMongoDao.updateMulti(query, entity, excludePropertyName);
    }

    public WriteResult updateMulti(Query query, @NonNull Map<String, Object> map, String... excludePropertyName) {
        return baseMongoDao.updateMulti(query, map, excludePropertyName);
    }

    public T updateOne(Query query, Update update) {
        return baseMongoDao.updateOne(query, update);
    }

    public void remove(Query query) {
        baseMongoDao.remove(query);
    }

    public long count(Query query) {
        return baseMongoDao.count(query);
    }

    /*****************************************华丽的分割线***************************************/

    public Optional<T> findByObjectIdToOptional(String objectId) {
        return Optional.ofNullable(baseMongoDao.findByObjId(objectId));
    }

    public Optional<List<T>> findToOptional(Query query) {
        return Optional.ofNullable(baseMongoDao.find(query));
    }

    public Optional<T> findOneToOptional(Query query) {
        return Optional.ofNullable(baseMongoDao.findOne(query));
    }

    public Optional<MongoPagination<T>> findPageToOptional(MongoPagination<T> page, Query query) {
        return Optional.ofNullable(baseMongoDao.findPage(query, page));
    }

    public Optional<List<T>> findAllToOptional() {
        return Optional.ofNullable(baseMongoDao.findAll());
    }
}
