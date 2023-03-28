package com.github.xiaour.easyexport.utils;

import com.github.xiaour.easyexport.constants.ExportConstant;
import com.github.xiaour.easyexport.model.EasyExportGroupMeta;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangtao
 *  导出类的一些基础工具方法，全局缓存数据
 * create by 2022/7/14 17:45
 */
public class EasyExportUtils {

    private EasyExportUtils() {
    }

    /**
     * 导出类的上下文
     */
    private static final Map<String,String> exportClazzContext = new HashMap<>();

    /**
     * 打包导出类的上下文
     */
    private static final Map<String, EasyExportGroupMeta> exportGroupContext = new HashMap<>();

    /**
     * 导出方法的参数集合
     */
    private final static Map<String,Class<?>[]> exportClazzParam = new HashMap<>();

    /**
     * 导出上下文
     * @return map
     */
    public static final Map<String, String> getExportClazzContext() {
        return exportClazzContext;
    }

    public static final Map<String, Class<?>[]> getExportClazzParam() {
        return exportClazzParam;
    }

    public static final Map<String,EasyExportGroupMeta> getExportGroupContext() {
        return exportGroupContext;
    }



    /**
     * 拆分传入的映射文件相关的参数
     * @param classMappingStr 对标文件
     * @return [exportClass,exportmethod]
     */
    public static String[] getClassMethod(String classMappingStr){
        //因为Base64前面添加了@用以识别所以这里需要替换掉。
        String classMapping = new String(Base64.getDecoder().decode(classMappingStr.replace(ExportConstant.EXPORT_CLASS_SPLIT_TAG,"")));
        String [] classMethod = classMapping.split(ExportConstant.EXPORT_CLASS_SPLIT_TAG);
        return  classMethod;
    }

}
