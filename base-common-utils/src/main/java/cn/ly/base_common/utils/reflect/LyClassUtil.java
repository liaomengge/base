package cn.ly.base_common.utils.reflect;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by liaomengge on 17/11/25.
 */
@UtilityClass
public class LyClassUtil {

    private final String CGLIB_CLASS_SEPARATOR = "$$";

    private final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

    {
        primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
        LyClassUtil.primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        LyClassUtil.primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        LyClassUtil.primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        LyClassUtil.primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        LyClassUtil.primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        LyClassUtil.primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        LyClassUtil.primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
    }

    private final String SETTER_PREFIX = "set";
    private final String GETTER_PREFIX = "get";
    private final String IS_PREFIX = "is";

    /************************************shortClassName 和 packageName********************************/

    /**
     * 返回Class名, 不包含PackageName.
     * <p>
     * 内部类的话, 返回"主类.内部类"
     */
    public String getShortClassName(Class<?> cls) {
        return ClassUtils.getShortClassName(cls);
    }

    /**
     * 返回Class名, 不包含PackageName
     * <p>
     * 内部类的话, 返回"主类.内部类"
     */
    public String getShortClassName(String className) {
        return ClassUtils.getShortClassName(className);
    }

    /**
     * 返回PackageName
     */
    public String getPackageName(Class<?> cls) {
        return ClassUtils.getPackageName(cls);
    }

    /**
     * 返回PackageName
     */
    public String getPackageName(String className) {
        return ClassUtils.getPackageName(className);
    }

    /************************************获取全部父类, 全部接口, 以及全部Annotation********************************/

    /**
     * 递归返回所有的SupperClasses, 包含Object.class
     */
    public List<Class<?>> getAllSuperclasses(Class<?> cls) {
        return ClassUtils.getAllSuperclasses(cls);
    }

    /**
     * 递归返回本类及所有基类继承的接口, 及接口继承的接口, 比Spring中的相同实现完整
     */
    public List<Class<?>> getAllInterfaces(Class<?> cls) {
        return ClassUtils.getAllInterfaces(cls);
    }

    /**
     * 递归Class所有的Annotation, 一个最彻底的实现.
     * <p>
     * 包括所有基类, 所有接口的Annotation, 同时支持Spring风格的Annotation继承的父Annotation,
     */
    public Set<Annotation> getAllAnnotations(Class<?> cls) {
        List<Class<?>> allTypes = getAllSuperclasses(cls);
        allTypes.addAll(getAllInterfaces(cls));
        allTypes.add(cls);

        Set<Annotation> anns = new HashSet<>();
        for (Class<?> type : allTypes) {
            anns.addAll(Arrays.asList(type.getDeclaredAnnotations()));
        }

        Set<Annotation> superAnnotations = new HashSet<>();
        for (Annotation ann : anns) {
            getSupperAnnotations(ann.annotationType(), superAnnotations);
        }

        anns.addAll(superAnnotations);

        return anns;
    }

    private <A extends Annotation> void getSupperAnnotations(Class<A> annotationType, Set<Annotation> visited) {
        Annotation[] anns = annotationType.getDeclaredAnnotations();

        for (Annotation ann : anns) {
            if (!ann.annotationType().getName().startsWith("java.lang") && visited.add(ann)) {
                getSupperAnnotations(ann.annotationType(), visited);
            }
        }
    }

    /**
     * 获取Annotation
     *
     * @param method
     * @param annotationType
     * @param <A>
     * @return
     */
    public <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        Class<?> targetClass = method.getDeclaringClass();
        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = org.springframework.util.ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        // 先找方法, 再找方法上的类
        A annotation = AnnotatedElementUtils.findMergedAnnotation(specificMethod, annotationType);
        if (null != annotation) {
            return annotation;
        }
        // 获取类上面的Annotation, 可能包含组合注解, 故采用spring的工具类
        return AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
    }

    /************************************获取标注了annotation的所有属性和方法********************************/

    /**
     * 找出所有标注了该annotation的公共属性, 循环遍历父类.
     * <p>
     * 暂未支持Spring风格Annotation继承Annotation
     * <p>
     * from org.unitils.util.AnnotationUtils
     */
    public <T extends Annotation> Set<Field> getAnnotatedPublicFields(Class<? extends Object> clz,
                                                                      Class<T> annotation) {

        if (Object.class.equals(clz)) {
            return Collections.emptySet();
        }

        Set<Field> annotatedFields = new HashSet<>();
        Field[] fields = clz.getFields();

        for (Field field : fields) {
            if (field.getAnnotation(annotation) != null) {
                annotatedFields.add(field);
            }
        }

        return annotatedFields;
    }

    /**
     * 找出所有标注了该annotation的属性, 循环遍历父类, 包含private属性.
     * <p>
     * 暂未支持Spring风格Annotation继承Annotation
     * <p>
     * from org.unitils.util.AnnotationUtils
     */
    public <T extends Annotation> Set<Field> getAnnotatedFields(Class<? extends Object> clz,
                                                                Class<T> annotation) {
        if (Object.class.equals(clz)) {
            return Collections.emptySet();
        }
        Set<Field> annotatedFields = new HashSet<>();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(annotation) != null) {
                annotatedFields.add(field);
            }
        }
        annotatedFields.addAll(getAnnotatedFields(clz.getSuperclass(), annotation));
        return annotatedFields;
    }

    /**
     * 找出所有标注了该annotation的公共方法(含父类的公共函数), 循环其接口.
     * <p>
     * 暂未支持Spring风格Annotation继承Annotation
     * <p>
     * 另, 如果子类重载父类的公共函数, 父类函数上的annotation不会继承, 只有接口上的annotation会被继承.
     */
    public <T extends Annotation> Set<Method> getAnnotatedPublicMethods(Class<?> clz, Class<T> annotation) {
        // 已递归到Objebt.class, 停止递归
        if (Object.class.equals(clz)) {
            return Collections.emptySet();
        }

        List<Class<?>> ifcs = ClassUtils.getAllInterfaces(clz);
        Set<Method> annotatedMethods = new HashSet<>();

        // 遍历当前类的所有公共方法
        Method[] methods = clz.getMethods();

        for (Method method : methods) {
            // 如果当前方法有标注, 或定义了该方法的所有接口有标注
            if (method.getAnnotation(annotation) != null || searchOnInterfaces(method, annotation, ifcs)) {
                annotatedMethods.add(method);
            }
        }

        return annotatedMethods;
    }

    private <T extends Annotation> boolean searchOnInterfaces(Method method, Class<T> annotationType,
                                                              List<Class<?>> ifcs) {
        for (Class<?> iface : ifcs) {
            try {
                Method equivalentMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                if (equivalentMethod.getAnnotation(annotationType) != null) {
                    return true;
                }
            } catch (NoSuchMethodException ex) {
                // Skip this interface - it doesn't have the method...
            }
        }
        return false;
    }

    /************************************获取方法********************************/

    /**
     * 循环遍历, 按属性名获取前缀为get或is的函数, 并设为可访问
     */
    public Method getSetterMethod(Class<?> clz, String propertyName, Class<?> parameterType) {
        String setterMethodName = LyClassUtil.SETTER_PREFIX + StringUtils.capitalize(propertyName);
        return LyClassUtil.getAccessibleMethod(clz, setterMethodName, parameterType);
    }

    /**
     * 循环遍历, 按属性名获取前缀为set的函数, 并设为可访问
     */
    public Method getGetterMethod(Class<?> clz, String propertyName) {
        String getterMethodName = LyClassUtil.GETTER_PREFIX + StringUtils.capitalize(propertyName);

        Method method = LyClassUtil.getAccessibleMethod(clz, getterMethodName);

        // retry on another name
        if (method == null) {
            getterMethodName = LyClassUtil.IS_PREFIX + StringUtils.capitalize(propertyName);
            method = LyClassUtil.getAccessibleMethod(clz, getterMethodName);
        }
        return method;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     * <p>
     * 因为class.getFiled(); 不能获取父类的private函数, 因此采用循环向上的getDeclaredField();
     */
    public Field getAccessibleField(Class clz, String fieldName) {
        Validate.notNull(clz, "clz can't be null");
        Validate.notEmpty(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = clz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                LyClassUtil.makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {// NOSONAR
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     * <p>
     * 匹配函数名+参数类型.
     * <p>
     * 因为class.getMethod() 不能获取父类的private函数, 因此采用循环向上的getDeclaredMethod();
     */
    public Method getAccessibleMethod(Class<?> clz, String methodName,
                                      Class<?>... parameterTypes) {
        Validate.notNull(clz, "class can't be null");
        Validate.notEmpty(methodName, "methodName can't be blank");
        Class[] theParameterTypes = ArrayUtils.nullToEmpty(parameterTypes);

        // 处理原子类型与对象类型的兼容
        LyClassUtil.wrapClassses(theParameterTypes);

        for (Class<?> searchType = clz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, theParameterTypes);
                LyClassUtil.makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     * <p>
     * 只匹配函数名, 如果有多个同名函数返回第一个
     * <p>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     * <p>
     * 因为class.getMethods() 不能获取父类的private函数, 因此采用循环向上的getMethods();
     */
    public Method getAccessibleMethodByName(Class clz, String methodName) {
        Validate.notNull(clz, "clz can't be null");
        Validate.notEmpty(methodName, "methodName can't be blank");

        for (Class<?> searchType = clz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    LyClassUtil.makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 改变private/protected的方法为public, 尽量不调用实际改动的语句, 避免JDK的SecurityManager抱怨。
     */
    public void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 改变private/protected的成员变量为public, 尽量不调用实际改动的语句, 避免JDK的SecurityManager抱怨。
     */
    public void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 兼容原子类型与非原子类型的转换, 不考虑依赖两者不同来区分不同函数的场景
     */
    private void wrapClassses(Class<?>[] source) {
        for (int i = 0; i < source.length; i++) {
            Class<?> wrapClass = primitiveWrapperTypeMap.get(source[i]);
            if (wrapClass != null) {
                source[i] = wrapClass;
            }
        }
    }

    /************************************杂项********************************/

    /**
     * From Spring, 按顺序获取默认ClassLoader
     * <p>
     * 1. Thread.currentThread().getContextClassLoader()
     * <p>
     * 2. ClassUtil的加载ClassLoader
     * <p>
     * 3. SystemClassLoader
     */
    public ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }

    /**
     * 获取CGLib处理过后的实体的原Class.
     */
    public Class<?> unwrapCglib(Object instance) {
        Validate.notNull(instance, "Instance must not be null");
        Class<?> clz = instance.getClass();
        if ((clz != null) && clz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clz.getSuperclass();
            if ((superClass != null) && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clz;
    }

    /**
     * 探测类是否存在classpath中
     */
    public boolean isPresent(String className, ClassLoader classLoader) {
        try {
            classLoader.loadClass(className);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }
}
