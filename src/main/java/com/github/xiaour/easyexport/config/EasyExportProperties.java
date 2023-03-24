package com.github.xiaour.easyexport.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author zhangtao
 * @Description 导出配置
 * @Date 2022/3/11 下午2:54
 */
@Configuration
@ConfigurationProperties(prefix = "com.github.xiaour.easyexport")
public class EasyExportProperties {



    /**
     * 文件存储位置
     */
    @Value("${com.github.xiaour.easyexport.file.path:}")
    private String filePath;

    /**
     * 上传完成后是否删除
     */
    @Value("${com.github.xiaour.easyexport.file.delete:false}")
    private Boolean isDelete;

    /**
     * 页码字段
     */
    @Value("${com.github.xiaour.easyexport.field.page.number:pageNum}")
    private String pageNumberField;

    /**
     * 每页记录数字段
     */
    @Value("${com.github.xiaour.easyexport.field.page.size:pageSize}")
    private String pageSizeField;

    /**
     * 每页查询记录数：默认每次查询1万条
     */
    @Value("${com.github.xiaour.easyexport.fetch.page.size:10000}")
    private Integer pageSize;

    /**
     * 文件每页写入记录数，超过限制写入下一个Sheet：默认每个sheet页写入150000
     */
    @Value("${com.github.xiaour.easyexport.file.sheet.size:150000}")
    private Integer sheetSize;

    public Integer getSheetSize() {
        return sheetSize;
    }

    public void setSheetSize(Integer sheetSize) {
        this.sheetSize = sheetSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNumberField() {
        return pageNumberField;
    }

    public void setPageNumberField(String pageNumberField) {
        this.pageNumberField = pageNumberField;
    }

    public String getPageSizeField() {
        return pageSizeField;
    }

    public void setPageSizeField(String pageSizeField) {
        this.pageSizeField = pageSizeField;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

}
