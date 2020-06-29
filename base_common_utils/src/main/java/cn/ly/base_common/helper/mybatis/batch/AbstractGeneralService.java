package cn.ly.base_common.helper.mybatis.batch;

import cn.ly.base_common.utils.log4j2.MwLogger;
import cn.ly.base_common.helper.mybatis.extension.MapResultHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by liaomengge on 16/6/23.
 */
public abstract class AbstractGeneralService {

    protected final Logger logger = MwLogger.getInstance(AbstractGeneralService.class);

    protected final int segmentNumber = 500;

    protected <T> void batchInsertEntry(SqlSessionFactory sqlSessionFactory, Class<? extends Mapper<T>> clazz,
                                        List<T> list) {
        batchInsertEntry(sqlSessionFactory, clazz, list, segmentNumber);
    }

    protected <T> void batchInsertEntry(SqlSessionFactory sqlSessionFactory, Class<? extends Mapper<T>> clazz,
                                        List<T> list, int size) {
        if (CollectionUtils.isEmpty(list) || size <= 0) {
            return;
        }
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, Boolean.FALSE);
            Mapper<T> mapper = sqlSession.getMapper(clazz);
            for (int i = 0, maxSize = list.size(); i < maxSize; i++) {
                T t = list.get(i);
                mapper.insertSelective(t);
                if ((i + 1) % size == 0 || i == maxSize - 1) {
                    sqlSession.commit();
                    //清理缓存, 防止溢出
                    sqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            logger.error("batch insert failed", e);
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    protected <T> void batchInsertEntry(SqlSessionFactory sqlSessionFactory, String statement, List<T> list) {
        batchInsertEntry(sqlSessionFactory, statement, list, segmentNumber);
    }

    protected <T> void batchInsertEntry(SqlSessionFactory sqlSessionFactory, String statement, List<T> list, int size) {
        if (CollectionUtils.isEmpty(list) || size <= 0) {
            return;
        }
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, Boolean.FALSE);
            for (int i = 0, maxSize = list.size(); i < maxSize; i++) {
                T t = list.get(i);
                sqlSession.insert(statement, t);
                if ((i + 1) % size == 0 || i == maxSize - 1) {
                    sqlSession.commit();
                    //清理缓存, 防止溢出
                    sqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            logger.error("batch insert failed", e);
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    protected <T> void batchUpdateEntry(SqlSessionFactory sqlSessionFactory, Class<? extends Mapper<T>> clazz,
                                        List<T> list) {
        batchUpdateEntry(sqlSessionFactory, clazz, list, segmentNumber);
    }

    protected <T> void batchUpdateEntry(SqlSessionFactory sqlSessionFactory, Class<? extends Mapper<T>> clazz,
                                        List<T> list, int size) {
        if (CollectionUtils.isEmpty(list) || size <= 0) {
            return;
        }
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, Boolean.FALSE);
            Mapper<T> mapper = sqlSession.getMapper(clazz);
            for (int i = 0, maxSize = list.size(); i < maxSize; i++) {
                T t = list.get(i);
                mapper.updateByPrimaryKeySelective(t);
                if ((i + 1) % size == 0 || i == maxSize - 1) {
                    sqlSession.commit();
                    //清理缓存, 防止溢出
                    sqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            logger.error("batch update failed", e);
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    protected <T> void batchUpdateEntry(SqlSessionFactory sqlSessionFactory, String statement, List<T> list) {
        batchUpdateEntry(sqlSessionFactory, statement, list, segmentNumber);
    }

    protected <T> void batchUpdateEntry(SqlSessionFactory sqlSessionFactory, String statement, List<T> list,
                                        int size) {
        if (CollectionUtils.isEmpty(list) || size <= 0) {
            return;
        }
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, Boolean.FALSE);
            for (int i = 0, maxSize = list.size(); i < maxSize; i++) {
                T t = list.get(i);
                sqlSession.update(statement, t);
                if ((i + 1) % size == 0 || i == maxSize - 1) {
                    sqlSession.commit();
                    //清理缓存, 防止溢出
                    sqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            logger.error("batch update failed", e);
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    protected <T> void batchDelEntry(SqlSessionFactory sqlSessionFactory, Class<? extends Mapper<T>> clazz, List<T>
            list) {
        batchDelEntry(sqlSessionFactory, clazz, list, segmentNumber);
    }

    protected <T> void batchDelEntry(SqlSessionFactory sqlSessionFactory, Class<? extends Mapper<T>> clazz, List<T>
            list, int size) {
        if (CollectionUtils.isEmpty(list) || size <= 0) {
            return;
        }
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, Boolean.FALSE);
            Mapper<T> mapper = sqlSession.getMapper(clazz);
            for (int i = 0, maxSize = list.size(); i < maxSize; i++) {
                T t = list.get(i);
                mapper.deleteByPrimaryKey(t);
                if ((i + 1) % size == 0 || i == maxSize - 1) {
                    sqlSession.commit();
                    //清理缓存, 防止溢出
                    sqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            logger.error("batch update failed", e);
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    protected <T> void batchDelEntry(SqlSessionFactory sqlSessionFactory, String statement, List<T> list) {
        batchDelEntry(sqlSessionFactory, statement, list, segmentNumber);
    }

    protected <T> void batchDelEntry(SqlSessionFactory sqlSessionFactory, String statement, List<T> list, int size) {
        if (CollectionUtils.isEmpty(list) || size <= 0) {
            return;
        }
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, Boolean.FALSE);
            for (int i = 0, maxSize = list.size(); i < maxSize; i++) {
                T t = list.get(i);
                sqlSession.delete(statement, t);
                if ((i + 1) % size == 0 || i == maxSize - 1) {
                    sqlSession.commit();
                    //清理缓存, 防止溢出
                    sqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            logger.error("batch update failed", e);
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            throw e;
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    protected <T extends Map<?, ?>, K, V> Map<K, V> queryForMap(SqlSessionFactory sqlSessionFactory, String statement, Object parameter,
                                                                MapResultHandler<T, K, V> handler) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            sqlSession.select(statement, parameter, handler);
        } catch (Exception e) {
            logger.error("query for map failed", e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return handler.getMappedResults();
    }

    protected <T extends Map<?, ?>, K, V> Map<K, V> queryForMap(SqlSessionFactory sqlSessionFactory, String statement, MapResultHandler<T, K,
            V> handler) {
        return this.queryForMap(sqlSessionFactory, statement, null, handler);
    }

}
