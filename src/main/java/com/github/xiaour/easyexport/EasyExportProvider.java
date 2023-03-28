package com.github.xiaour.easyexport;


import com.alibaba.fastjson.JSON;
import com.github.xiaour.easyexport.annotation.EasyExportSingle;
import com.github.xiaour.easyexport.builder.ExportContext;
import com.github.xiaour.easyexport.builder.ExportParam;
import com.github.xiaour.easyexport.config.EasyExportProperties;
import com.github.xiaour.easyexport.constants.ExportConstant;
import com.github.xiaour.easyexport.context.AppContextFactory;
import com.github.xiaour.easyexport.exception.EasyExportException;
import com.github.xiaour.easyexport.model.ExportMapping;
import com.github.xiaour.easyexport.utils.EasyExportUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author zhangtao
 *  公共导出类
 * create by 2022/3/11 下午3:43
 */
@Slf4j
public class EasyExportProvider extends AbsExportExecutor {

    public EasyExportProvider(AppContextFactory appContextFactory, EasyExportProperties properties, Lock lock) {
        super(appContextFactory, properties,lock);
    }


    @SneakyThrows
    @Override
    public Long getTotal(ExportContext exportContext) {
        Object obj = getDataList(exportContext,1);
        Assert.notNull(obj,"未获取到总数，对象为空");
        Method m = obj.getClass().getMethod(StringUtils.isEmpty(exportContext.getDataTotalMethod())? ExportConstant.METHOD_TOTAL:exportContext.getDataTotalMethod());
        Long count = (Long) m.invoke(obj);
        if(count >= ExportConstant.MAX_SIZE){
            throw new EasyExportException("本次导出总记录数不能大于"+ExportConstant.MAX_SIZE+"条，请进行条件限制后重试！");
        }
        return count;
    }

    @SneakyThrows
    @Override
    public List<?> getNextPage(ExportContext exportContext, int pageNum) {
        Object obj = getDataList(exportContext,pageNum);
        Assert.notNull(obj,"未获取到记录，对象为空");
        Method m = obj.getClass().getMethod(StringUtils.isEmpty(exportContext.getDataListMethod())? ExportConstant.METHOD_LIST:exportContext.getDataListMethod());
        List<?> result = (List)m.invoke(obj);
        return result;
    }

    /**
     * 获取所有的导出类映射文件
     * @return List
     */
    public List<ExportMapping> getMappingClass(){
        List<ExportMapping> list = new ArrayList<>();
        //加密处理
        Map<String, String> singleExportMap = EasyExportUtils.getExportClazzContext();
        for(Map.Entry<String, String> set:singleExportMap.entrySet()){
            list.add(new ExportMapping(set.getKey(), ExportConstant.EXPORT_CLASS_SPLIT_TAG+Base64.getEncoder()
                    .encodeToString( set.getValue().getBytes())));
        }
        return list;
    }

    @SneakyThrows
    private Object[] paramBuilder(ExportContext context,String classMapping,Integer pageNum) {
        log.info("开始转换导出方法的参数");

        Class<?>[] classArray = EasyExportUtils.getExportClazzParam().get(classMapping);


        List list = new ArrayList<>();

        if (!ObjectUtils.isEmpty(context.getExportParam())) {

            for (int i = 0; i < classArray.length; i++) {

                if(classArray[i].getName().equals(context.getUserClass().getName())){
                    //兼容老版本用户信息
                    list.add(context.getCurrentUser());
                }else{
                    String jsonStr = JSON.toJSONString(context.getExportParam().getParams());
                    Object object = JSON.parseObject(jsonStr, classArray[i]);
                    this.resetPageSize(object,pageNum);
                    list.add(object);
                }

            }
        }
        return list.toArray();
    }


    private Object getDataList(ExportContext exportContext,int pageNum){
        try {
            log.info("开始执行导出方法的查询");

            ExportParam param = exportContext.getExportParam();
            //取出导出映射类并Base64解码
            String classMapping = new String(Base64.getDecoder().decode(param.getClazzMapping().replace(ExportConstant.EXPORT_CLASS_SPLIT_TAG,"")));

            String [] classMethod = EasyExportUtils.getClassMethod(param.getClazzMapping());

            Class<?> exportClass = Class.forName(classMethod[0]);
            //获取这个类的导出方法
            Method method = exportClass.getMethod(classMethod[1], EasyExportUtils.getExportClazzParam().get(classMapping));

            EasyExportSingle annotation = method.getAnnotation(EasyExportSingle.class);

            if (annotation != null) {

                Object bean = this.appContextFactory().getBean(exportClass);

                Object pageInvoke = method.invoke(bean, paramBuilder(exportContext,classMapping,pageNum));

                return pageInvoke;
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.error("IllegalAccessException:{}",e);
            throw new EasyExportException(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            log.error("InvocationTargetException:{}",e);
            throw new EasyExportException(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("ClassNotFoundException:{}",e);
            throw new EasyExportException(e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            log.error("NoSuchMethodException:{}",e);
            throw new EasyExportException(e.getMessage());
        }
        return null;
    }


    /**
     * 将参数中的每页记录数放大
     * @param obj 参数对象
     * @param pageNum 页码
     * @throws IllegalAccessException
     */
    private void resetPageSize(Object obj,Integer pageNum) throws IllegalAccessException {
        Class clazz = obj.getClass();

        boolean flag;

        Field[] fields = clazz.getDeclaredFields();

        flag = resetFieldValue(fields,obj,pageNum);

        int count = 0;
        //8是为了防止重置分页参数时出现死循环特定了一个值
        while (clazz != null && !flag && count < 8){

            clazz = clazz.getSuperclass();

            if(clazz != null){
                fields = clazz.getDeclaredFields();
                flag = resetFieldValue(fields,obj,pageNum);
                count ++;
            }
        }
    }

    /**
     * 重置分页字段的值，放大每次获取数据量
     * @param fields
     * @param obj
     * @param pageNum
     * @return
     * @throws IllegalAccessException
     */
    private boolean resetFieldValue(Field [] fields,Object obj,Integer pageNum) throws IllegalAccessException {
        //判断是否重置了两个分页的字段
        int count = 0;

        for (Field f:fields){
            f.setAccessible(true);
            if(f.getName().equals(this.getProperties().getPageSizeField())){
                f.set(obj, this.getProperties().getPageSize());
                count++;
            }
            if(f.getName().equals(this.getProperties().getPageNumberField())){
                f.set(obj,pageNum);
                count++;
            }
        }
        return count>1?true:false;
    }

}
