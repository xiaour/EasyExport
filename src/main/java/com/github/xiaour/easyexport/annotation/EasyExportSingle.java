package com.github.xiaour.easyexport.annotation;

import java.lang.annotation.*;

/**
 * @Author zhangtao
 * @Description EasyExportMethod 是针对每个导出注解Service使用 单个导出
 * @Date 2022/1/6 下午7:46
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyExportSingle {
    /**
     * 导出表格表头名称
     * @return
     */
    String value();

    /**
     * 导出的模板类
     * @return
     */
    Class<?>  modelClass();
}
