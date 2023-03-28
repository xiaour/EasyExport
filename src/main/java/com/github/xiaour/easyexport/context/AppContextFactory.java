package com.github.xiaour.easyexport.context;

import com.github.xiaour.easyexport.annotation.EasyExport;
import com.github.xiaour.easyexport.annotation.EasyExportGroup;
import com.github.xiaour.easyexport.annotation.EasyExportSingle;
import com.github.xiaour.easyexport.constants.ExportConstant;
import com.github.xiaour.easyexport.event.AbsBaseEvent;
import com.github.xiaour.easyexport.exception.EasyExportException;
import com.github.xiaour.easyexport.model.EasyExportGroupMeta;
import com.github.xiaour.easyexport.utils.EasyExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * AppContext
 *
 * @Version 1.1.0
 * @Author zhang.tao
 * @Date 2020/12/10 11:55
 * @Description 应用的自定义全局上下文包括一些关键的全局方法
 */
@Slf4j
@Component
public class AppContextFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static final ThreadLocal<Map<String, Object>> threadVar = new ThreadLocal<>();

    public static void clearThreadContext() {
        threadVar.remove();
    }

    /**
     * 发送异步事件
     * @param event
     */
    @Async
    public  void publish(AbsBaseEvent event) {
        event.setThreadVar(threadVar.get());

       /* //避免异步事件调用的时候事务还未提交导致的问题 如果开启事物了(如果是同步则让他们都在一个事物里)
        if(TransactionSynchronizationManager.isSynchronizationActive()){
            //在事物执行成功后发布事件
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    applicationContext.publishEvent(event);
                }
            });
        }else{
            applicationContext.publishEvent(event);
        }*/

        applicationContext.publishEvent(event);

    }

    public static void putAllThreadContext(Map<String, Object> varMap) {
        if (varMap != null && varMap.size() > 0) {
            threadVar.set(varMap);
        }
    }

    public static void putThreadContext(String key, Object value) {
        Map<String, Object> threadMap = threadVar.get();
        if (threadMap == null) {
            threadMap = new HashMap<>();
            threadVar.set(threadMap);
        }

        threadMap.put(key, value);
    }

    public static Object getThreadContext(String key) {
        Map<String, Object> threadMap = threadVar.get();
        return threadMap == null ? null : threadMap.get(key);
    }

    public static void removeThreadContext(String key) {
        Map<String, Object> threadMap = threadVar.get();
        if (threadMap != null) {
            threadMap.remove(key);
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.getExportClassByAnnotation(applicationContext);
        log.info("AppContext initialized.");
        this.applicationContext = applicationContext;
    }

    private void getExportClassByAnnotation(ApplicationContext context) {
        Map<String, Object> exportClazzMap = context.getBeansWithAnnotation(EasyExport.class);
        if(exportClazzMap == null || exportClazzMap.isEmpty()){
           log.warn("not found mapping files.");
            return;
        }
        exportClazzMap.entrySet().forEach(exportClazz ->{
            try {
                getExportAnnotationMethod(exportClazz.getValue().getClass());
            } catch (Exception e) {
                log.error("Mapping class init error,{}",e);
            }
        });

        log.info("scan to "+EasyExportUtils.getExportClazzContext().size()+"mapping files.");

    }

    /**
     * 获取导出注解的相关方法
     * @param clz
     * @throws Exception
     */
    public void getExportAnnotationMethod(Class clz) {
        //因为是AOP代理的类，所以不能直接实例化获取注解
        Class clazz = ClassUtils.getUserClass(clz);
        for (Method method : clazz.getDeclaredMethods()) {
            EasyExportSingle easyExportSingle = method.getAnnotation(EasyExportSingle.class);
            EasyExportGroup easyExportGroup  = method.getAnnotation(EasyExportGroup.class);

            if (easyExportSingle != null) {
                EasyExportUtils.getExportClazzParam().put(clazz.getCanonicalName()+ ExportConstant.EXPORT_CLASS_SPLIT_TAG+method.getName(),method.getParameterTypes());
                EasyExportUtils.getExportClazzContext().put(easyExportSingle.value(),clazz.getCanonicalName()+ ExportConstant.EXPORT_CLASS_SPLIT_TAG+method.getName());
            }

            if (easyExportGroup != null) {
                EasyExportUtils.getExportClazzParam().put(clazz.getCanonicalName()+ ExportConstant.EXPORT_CLASS_SPLIT_TAG+method.getName(),method.getParameterTypes());
                EasyExportUtils.getExportGroupContext().put(easyExportGroup.groupName(),this.getExportGroup(easyExportGroup.groupName(),clazz.getCanonicalName(),method.getName()));
            }
        }
    }

    /**
     * 获取导出的Map，如果没有新建一个
     * @param groupName
     * @param exportClass
     * @return Map<String,List<String>>
     */
    private EasyExportGroupMeta getExportGroup(String groupName, String exportClass, String exportMethod){
        if(EasyExportUtils.getExportGroupContext()!= null){
            EasyExportGroupMeta meta = EasyExportUtils.getExportGroupContext().get(groupName);
            if (meta == null) {
                meta = new EasyExportGroupMeta();
            }
            meta.setExportClass(exportClass);
            meta.setExportMethod(exportMethod);
            return meta;
        }
        throw new EasyExportException("container was not initialized!");
    }



    public final ApplicationContext getApplicationContext() {
        Assert.notNull(applicationContext, "applicationContext is not null");
        return applicationContext;
    }

    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public Object getBean(String clazzName) {
        return applicationContext.getBean(clazzName);
    }


}
