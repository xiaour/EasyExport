package com.github.xiaour.easyexport.builder;

import com.github.xiaour.easyexport.annotation.EasyExportSingle;
import com.github.xiaour.easyexport.converter.ExportConverter;
import com.github.xiaour.easyexport.handler.ModelHandler;
import com.github.xiaour.easyexport.utils.EasyExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * @Author zhangtao
 * @Description The author is lazy and doesn't write anything.
 * @Date 2022/3/29 下午6:05
 */
@Slf4j
public class EasyExportBuilder<T> {
    /**
     * 是否删除文件
     */
    private boolean isDeleteFile;

    /**
     * 任务上下文
     */
    private ExportContext exportContext;


    /**
     * EasyExcel转换类
     */
    private ExportConverter<T> converter;


    public ExportConverter<T> getConverter() {
        return converter;
    }

    public EasyExportBuilder isDeleteFile(final boolean isDeleteFile) {
        this.isDeleteFile = isDeleteFile;
        return this;
    }


    public EasyExportBuilder context(final ExportContext exportContext) {
        this.exportContext = exportContext;
        this.init();
        return this;
    }

    /**
     * 注册转换器，EasyExcel原生转换器
     * @param converter
     * @return
     */
    public EasyExportBuilder<T> registerConverter(final ExportConverter<T> converter) {
        this.converter = converter;
        return this;
    }


    /**
     * 导出的主要入口逻辑,execute()和upload()需要保持先后顺序，否则取不到localFile
     */
    private EasyExportBuilder init() {

        Assert.notNull(this.getExportContext(),"exportContext should not be null");

        ExportContext context = this.getExportContext();

        String [] classMethod = EasyExportUtils.getClassMethod(context.getExportParam().getClazzMapping());

        Class<?> exportClass;
        try {
            exportClass = Class.forName(classMethod[0]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("导出失败，无法实例化相关的业务类,{}",e);
            return null;
        }

        Assert.notNull(exportClass,"导出的模板类未配置或该Class不存在，无法导出:"+classMethod[0]);

        Class exportModelClazz = null;

        for (Method method : exportClass.getDeclaredMethods()) {
            if (method.getAnnotation(EasyExportSingle.class) != null && method.getName().equals(classMethod[1])) {
                exportModelClazz = method.getAnnotation(EasyExportSingle.class).modelClass();
            }
        }
        Assert.notNull(exportModelClazz,"未配置ModelClass:"+exportModelClazz.getName()+"无法导出！");
        //设置导出的Model类
        context.excelHeaderClazz(exportModelClazz);

        return this;
    }


    /**
     * 处理Model，改变字段顺序及中文列名等转换操作
     * @param initCallback
     * @return
     */
    public EasyExportBuilder modelHandler(ModelHandler initCallback){
        Assert.notNull(this.getExportContext(),"exportContext should not be null");
        initCallback.initModel();
        return this;
    }

    public boolean isDeleteFile() {
        return isDeleteFile;
    }

    public ExportContext getExportContext() {
        return exportContext;
    }


    public void clearExportContext() {
        exportContext = null;
    }


    public static EasyExportBuilder builder(){
        return new EasyExportBuilder();
    }
}
