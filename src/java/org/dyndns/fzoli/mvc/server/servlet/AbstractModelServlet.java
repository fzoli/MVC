package org.dyndns.fzoli.mvc.server.servlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dyndns.fzoli.mvc.server.model.Model;
import org.dyndns.fzoli.mvc.server.model.annotation.UseModelMap;
import org.dyndns.fzoli.mvc.server.model.bean.ModelBean;
import org.dyndns.fzoli.mvc.server.model.map.ModelMap;
import org.dyndns.fzoli.mvc.server.servlet.key.BeanKeys;
import org.dyndns.fzoli.mvc.server.servlet.util.ServletUtils;
import org.dyndns.fzoli.servlet.AbstractPrinterServlet;
import org.scannotation.AnnotationDB;
import org.scannotation.WarUrlFinder;

/**
 *
 * @author zoli
 */
public abstract class AbstractModelServlet<EventType, PropsType> extends AbstractPrinterServlet implements ModelServlet<EventType, PropsType> {
    
    private static String mapName = null;
    private final ServletUtils<EventType, PropsType> UTILS = ServletUtils.create(this);
    
    @Override
    protected void printResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UTILS.printResponse(request, response);
    }
    
    /**
     * Returns the ModelBean and create one if it is not exists.
     * @param request
     * @return ModelBean
     */
    @Override
    public ModelBean<EventType, PropsType> getModelBean(HttpServletRequest request) {
        return (ModelBean<EventType, PropsType>) getSessionObject(request, getModelBeanId());
    }
    
    /**
     * You can access models directly from this map if JavaScript not enabled.
     * @param request
     * @return 
     */
    @Override
    public ModelMap<Model<EventType, PropsType>> getModelMap(HttpServletRequest request) {
        return (ModelMap<Model<EventType, PropsType>>) getSessionObject(request, getModelMapId());
    }
    
    private Object getSessionObject(HttpServletRequest request, String key) {
        Object obj = null;
        try {
            obj = getSessionAttribute(request, key);
        }
        catch(Exception ex) {}
        finally {
            if (obj == null) { //if cookie is empty
                if (key.equals(getModelBeanId()))
                    return createModelBean(request);
                if (key.equals(getModelMapId()))
                    return createModelMap(request);
            }
        }
        return obj;
    }
    
    private ModelBean<EventType, PropsType> createModelBean(HttpServletRequest request) {
        ModelMap<Model<EventType, PropsType>> map = getModelMap(request);
        ModelBean<EventType, PropsType> modelBean = new ModelBean<EventType, PropsType>(this, request.getSession(true), map);
        setSessionAttribute(request, getModelBeanId(), modelBean);
        return modelBean;
    }
    
    private String getModelBeanId() {
        return getCtxInitParameter("model_bean_id", BeanKeys.ID_MODEL_BEAN);
    }
    
    private String getModelMapId() {
        return getCtxInitParameter("model_map_id", BeanKeys.ID_MODEL_MAP);
    }

    private ModelMap<Model<EventType, PropsType>> createModelMap(HttpServletRequest request) {
        ModelMap<Model<EventType, PropsType>> map = (ModelMap<Model<EventType, PropsType>>) createObject(createModelMapClassName());
        setSessionAttribute(request, getModelMapId(), map);
        return map;
    }
    
    private String createModelMapClassName() {
        if (mapName != null) return mapName;
        String name = getCtxInitParameter("model_map_class_name");
        if (name == null) name = findFirstAnnotatedModelMapClassName();
        if (name == null) throw new RuntimeException("Model map class name is not specified");
        mapName = name;
        return name;
    }
    
    private String findFirstAnnotatedModelMapClassName() {
        String clazz = null;
        try {
            URL url = WarUrlFinder.findWebInfClassesPath(getServletContext());
            AnnotationDB db = new AnnotationDB();
            db.setScanFieldAnnotations(false);
            db.setScanMethodAnnotations(false);
            db.setScanParameterAnnotations(false);
            db.scanArchives(url);
            Map<String, Set<String>> map = db.getAnnotationIndex();
            Set<String> set = map.get(UseModelMap.class.getName());
            if (set != null) {
                Iterator<String> it = set.iterator();
                while (it.hasNext()) {
                    clazz = it.next();
                    break;
                }
            }
        }
        catch (Exception ex) {
            ;
        }
        return clazz;
    }
    
    private Object createObject(String className) {
        try {
            Class<?> cl = Class.forName(className);
            Constructor<?> co = cl.getConstructor(new Class[] {});
            return co.newInstance(new Object[] {});
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
}