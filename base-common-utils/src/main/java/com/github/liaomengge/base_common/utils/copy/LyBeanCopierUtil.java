package com.github.liaomengge.base_common.utils.copy;

import com.github.liaomengge.base_common.utils.collection.LyMapUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Created by liaomengge on 16/7/28.
 */
@Slf4j
@UtilityClass
public class LyBeanCopierUtil {
    
    private final Map<String, BeanCopier> beanCopierMap = new ConcurrentHashMap<>(16);

    /**
     * <p>
     * 效率比BeanUtils和json序列化快, 使用时应注意如下
     * 缺陷:
     * 1. 源类和目标类的属性一致,但是类型不一致(int和Integer,不能被拷贝)
     * 2. Get和Set方法不匹配(个数不一致)
     * 3. 特殊target类,比如Thrift,不能进行拷贝
     * <p>
     *
     * @param source
     * @param target
     * @param iBeanCopier
     * @param <S>
     * @param <T>
     */
    public <S, T> void copyProperties(S source, T target, IBeanCopier<S, T> iBeanCopier) {
        if (source == null || target == null) {
            return;
        }

        Class<?> sourceCls = source.getClass();
        Class<?> targetCls = target.getClass();
        String beanKey = generateKey(sourceCls, targetCls);
        try {
            BeanCopier copier = LyMapUtil.computeIfAbsent(beanCopierMap, beanKey,
                    val -> BeanCopier.create(sourceCls, targetCls, false));

            copier.copy(source, target, null);

            if (iBeanCopier != null) {
                iBeanCopier.afterCopy(source, target);
            }
        } catch (Exception e) {
            log.error("copy bean error", e);
            BeanUtils.copyProperties(source, target);
        }
    }

    public <S, T> void copyProperties(S source, T target) {
        copyProperties(source, target, null);
    }

    public <S, T> void copyProperties2(S source, T target, BiConsumer<S, T> biConsumer) {
        if (source == null || target == null) {
            return;
        }

        Class<?> sourceCls = source.getClass();
        Class<?> targetCls = target.getClass();
        String beanKey = generateKey(sourceCls, targetCls);
        try {
            BeanCopier copier = LyMapUtil.computeIfAbsent(beanCopierMap, beanKey,
                    val -> BeanCopier.create(sourceCls, targetCls, false));

            copier.copy(source, target, null);

            if (biConsumer != null) {
                biConsumer.accept(source, target);
            }
        } catch (Exception e) {
            log.error("copy bean error", e);
            BeanUtils.copyProperties(source, target);
        }
    }

    public <S, T> void copyProperties2(S source, T target) {
        copyProperties2(source, target, null);
    }

    private String generateKey(Class<?> class1, Class<?> class2) {
        return class1.getName() + "-" + class2.getName();
    }

    @FunctionalInterface
    public interface IBeanCopier<S, T> {
        void afterCopy(S source, T target);
    }
}
