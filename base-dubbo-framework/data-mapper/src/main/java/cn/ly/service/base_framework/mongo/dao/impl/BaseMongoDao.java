package cn.ly.service.base_framework.mongo.dao.impl;

import cn.ly.base_common.utils.generic.LyGenericUtil;
import cn.ly.service.base_framework.mongo.dao.IBaseMongoDao;
import cn.ly.service.base_framework.mongo.domain.BaseMongoDoc;
import cn.ly.service.base_framework.mongo.page.MongoPagination;
import cn.ly.service.base_framework.mongo.util.LyMongoUtil;
import com.mongodb.client.result.UpdateResult;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;


/**
 * Created by liaomengge on 17/3/2.
 */
public abstract class BaseMongoDao<T extends BaseMongoDoc> implements IBaseMongoDao<T> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Override
    public T insert(T entity) {
        mongoTemplate.insert(entity);
        return entity;
    }

    @Override
    public void batchInsert(List<T> list) {
        mongoTemplate.insertAll(list);
    }

    @Override
    public void batchInsert(List<T> list, int batchSize) {
        List<T> subList;
        while (!list.isEmpty()) {
            subList = list.subList(0, Math.min(batchSize, list.size()));
            mongoTemplate.insertAll(subList);
            subList.clear();
        }
    }

    @Override
    public T findByObjId(String objId) {
        return mongoTemplate.findById(objId, getEntityClass());
    }

    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, getEntityClass());
    }

    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, getEntityClass());
    }

    @Override
    public MongoPagination<T> findPage(Query query, MongoPagination<T> page) {
        long count = count(query);
        page.setTotalCount(count);
        int pageSize = page.getPageSize();
        int skip = page.getSkip();
        query.skip(skip).limit(pageSize);
        List<T> result = find(query);
        page.build(result);
        return page;
    }

    @Override
    public List<T> findAll() {
        return mongoTemplate.findAll(getEntityClass());
    }

    @Override
    public UpdateResult updateFirst(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, getEntityClass());
    }

    @Override
    public UpdateResult updateFirst(Query query, @NonNull T entity, String... excludePropertyName) {
        Update update = LyMongoUtil.convertObjectToParams(entity, excludePropertyName);
        return mongoTemplate.updateFirst(query, update, getEntityClass());
    }

    @Override
    public UpdateResult updateFirst(Query query, @NonNull Map<String, Object> map, String... excludePropertyName) {
        Update update = LyMongoUtil.convertMapToParams(map, excludePropertyName);
        return mongoTemplate.updateFirst(query, update, getEntityClass());
    }

    @Override
    public UpdateResult updateMulti(Query query, Update update) {
        return mongoTemplate.updateMulti(query, update, getEntityClass());
    }

    @Override
    public UpdateResult updateMulti(Query query, @NonNull T entity, String... excludePropertyName) {
        Update update = LyMongoUtil.convertObjectToParams(entity, excludePropertyName);
        return mongoTemplate.updateMulti(query, update, getEntityClass());
    }

    @Override
    public UpdateResult updateMulti(Query query, @NonNull Map<String, Object> map, String... excludePropertyName) {
        Update update = LyMongoUtil.convertMapToParams(map, excludePropertyName);
        return mongoTemplate.updateMulti(query, update, getEntityClass());
    }

    @Override
    public T updateOne(Query query, Update update) {
        return mongoTemplate.findAndModify(query, update, getEntityClass());
    }

    @Override
    public void remove(Query query) {
        mongoTemplate.remove(query, getEntityClass());
    }

    @Override
    public long count(Query query) {
        return mongoTemplate.count(query, getEntityClass());
    }

    private Class<T> getEntityClass() {
        return LyGenericUtil.getGenericClassType(getClass());
    }
}
