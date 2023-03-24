package com.github.xiaour.easyexport.model;

import lombok.Data;

/**
 * @Author zhangtao
 * @Description 导出的映射文件键值对结构
 * @Date 2022/7/25 17:37
 */
@Data
public class ExportMapping {

    /**
     *键
     */
    private String key;

    /**
     * 值
     */
    private String value;

    public ExportMapping(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
