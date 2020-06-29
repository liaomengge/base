package cn.mwee.base_common.utils.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liaomengge on 17/11/7.
 */
public final class MwXmlUtil {

    private static Map<Class<?>, XStream> xstreamMap = new HashMap<>(16);

    private MwXmlUtil() {
    }

    /**
     * 构建XStream
     *
     * @param cls
     * @return
     */
    public static XStream getXstream(Class<?> cls) {
        return getXstream(cls, new Xpp3DomDriver());
    }

    /**
     * 构建XStream
     *
     * @param cls
     * @param hierarchicalStreamDriver
     * @return
     */
    public static XStream getXstream(Class<?> cls, HierarchicalStreamDriver hierarchicalStreamDriver) {
        if (xstreamMap.containsKey(cls)) {
            return xstreamMap.get(cls);
        }

        XStream xstream = new XStream(hierarchicalStreamDriver);
        xstream.processAnnotations(cls);

        xstreamMap.put(cls, xstream);

        return xstream;
    }

    /**
     * xml反序列化操作
     *
     * @param xmlStr
     * @param cls    反序列化的对象
     * @return
     */
    public static <T> T toBean(String xmlStr, Class<T> cls) {
        XStream xstream = getXstream(cls);
        T t = (T) xstream.fromXML(xmlStr);
        return t;
    }

    /**
     * xml反序列化操作
     *
     * @param xmlStr
     * @param cls     反序列化的对象
     * @param implCls 反序列化过程中有继承关系的对象
     * @return
     */
    public static <T> T toBean(String xmlStr, Class<T> cls, Class<?>... implCls) {
        XStream xstream = getXstream(cls);
        xstream.ignoreUnknownElements();
        for (Class<?> implCl : implCls) {
            xstream.addDefaultImplementation(implCl, implCl.getSuperclass());
            xstream.processAnnotations(implCl);
        }
        T t = (T) xstream.fromXML(xmlStr);
        return t;
    }

    /**
     * xml反序列化操作
     *
     * @param xmlStr
     * @param cls     反序列化的对象
     * @param implCls 反序列化过程中有继承关系的对象
     * @param types   子对象需要沿用注解的对象
     * @return
     */
    public static <T> T toBean(String xmlStr, Class<T> cls, List<Class<?>> implCls, Class<?>... types) {
        XStream xstream = getXstream(cls);
        for (Class<?> implCl : implCls) {
            xstream.addDefaultImplementation(implCl, implCl.getSuperclass());
        }
        for (Class<?> type : types) {
            xstream.processAnnotations(type);
        }
        T t = (T) xstream.fromXML(xmlStr);
        return t;
    }

    /**
     * xml序列化操作
     *
     * @param t 序列化对象
     * @return
     */
    public static <T> String toXml(T t) {
        XStream xstream = getXstream(t.getClass());
        StringWriter sw = new StringWriter();
        xstream.marshal(t, new CompactWriter(sw));
        return sw.toString();
    }


    public static <T> String toXml(T t, Class<?>... implClses) {
        XStream xstream = getXstream(t.getClass());
        for (Class<?> implCls : implClses) {
            xstream.addDefaultImplementation(implCls, implCls.getSuperclass());
            xstream.processAnnotations(implCls);
        }
        StringWriter sw = new StringWriter();
        xstream.marshal(t, new CompactWriter(sw));
        return sw.toString();
    }

    /**
     * @param t
     * @param implCls
     * @return
     */
    public static <T> String toXml(T t, Set<Class<?>> implCls) {
        XStream xstream = getXstream(t.getClass());
        for (Class<?> implCl : implCls) {
            xstream.processAnnotations(implCl);
            XStreamAlias[] classAnnotation = implCl.getAnnotationsByType(XStreamAlias.class);
            for (XStreamAlias an : classAnnotation) {
                xstream.alias(an.value(), implCl);
            }
        }
        StringWriter sw = new StringWriter();
        xstream.marshal(t, new CompactWriter(sw, new XmlFriendlyNameCoder("_-", "_")));
        return sw.toString();
    }

}
