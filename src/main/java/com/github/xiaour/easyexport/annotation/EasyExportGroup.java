package com.github.xiaour.easyexport.annotation;

import java.lang.annotation.*;

/**
 * @author zhangtao
 *  分组打包导出，
 * 注意：1.如果需要打包导出，多个方法的入参必须一致，否则无法导出。
 *      2.同一组导出必须将实现放在同一个具体实现中，否则无法识别，即多个实现类中的分组导出名称可以重复，互不影响。
 * create by 2022/6/15 下午7:46
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyExportGroup {
    /**
     * 导出表格表头ID
     * @return 自定义参数
     */
    String value();

    /**
     * 如果两个文件需要放到一个压缩文件中，分组别名必须一致
     * @return 分组别名
     */
    String groupName();

    /**
     * 使用模型中定义的index，反之按照数据库中的顺序
     * @return boolean
     */
    boolean  useModelIndex() default false;

    /**
     * 导出的模板类
     * @return 模板类
     */
    Class<?>  modelClass();
}
