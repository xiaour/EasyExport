package com.github.xiaour.easyexport.annotation;

import java.lang.annotation.*;

/**
 * @author zhangtao
 *  导出类必须注入，不注入无法发现此导出类
 * create by 2021/11/11 下午4:33
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyExport {
    /**
     * 模块名称
     * @return String
     */
    String value() default "";
}

