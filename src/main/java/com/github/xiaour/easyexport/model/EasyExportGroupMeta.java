package com.github.xiaour.easyexport.model;

import lombok.Data;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author zhangtao
 *  The author is lazy and doesn't write anything.
 * create by 2022/6/21 上午10:00
 */
@Data
public class EasyExportGroupMeta {

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 导出类名
     */
    private String exportClass;

    /**
     * 导出方法
     */
    private Set<String> exportMethod;



    public void setExportMethod(String method) {
        if(this.exportMethod == null){
            this.exportMethod = new TreeSet<>();
        }
        this.exportMethod.add(method);
    }
}
