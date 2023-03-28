package com.github.xiaour.easyexport.model;

import lombok.Data;

/**
 * @author zhangtao
 *  导出方法模板
 * create by 2022/6/27 下午4:41
 */
@Data
public class ExportGroupModel {
    /**
     * 导出文件名称
     */
    private String exportName;
    /**
     * 导出的Model，模板类
     */
    private Class<?> modelClass;

    /**
     * 执行导出的类"Service"
     */
    private Class<?>  exportClass;
    /**
     * 执行导出的类"Service"的查询方法
     */
    private String exportMethod;
}
