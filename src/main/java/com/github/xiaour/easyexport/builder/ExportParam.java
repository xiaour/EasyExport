package com.github.xiaour.easyexport.builder;

import org.springframework.util.Assert;

import java.util.HashMap;

/**
 * @author zhangtao
 *  统一的导出参数模板类，需要在调度创建的接口初始化，clazzMapping非常重要，后端需要提供一个接口AppContext.getExportClazzContext()获取对标文件
 * create by 2022/3/29 下午6:43
 */
public class ExportParam {

    /**
     * 页面地址，主要用来输出跟踪信息，可以是页面地址URL或者页面的ID
     */
    private String pageId;

    /**
     * 导出的所有参数，和查询参数一致。
     */
    private HashMap<?,?> params;

    /**
     * 执行导出的对标文件，可以通过AppContext.getExportClazzContext();获取到所有的导出对标文件
     * */
    private String clazzMapping;


    public static ExportParam builder(){
        return new ExportParam();
    }


    public ExportParam pageId(final String pageId) {
        this.pageId = pageId;
        Assert.notNull(pageId,"pageId should not be null");
        return this;
    }

    public ExportParam params(final HashMap<?,?> params) {
        this.params = params;
        Assert.notNull(params,"params should not be null");
        return this;
    }

    public ExportParam clazzMapping(final String clazzMapping) {
        this.clazzMapping = clazzMapping;
        Assert.notNull(clazzMapping,"clazzMapping should not be null");
        return this;
    }


    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public HashMap<?, ?> getParams() {
        return params;
    }

    public void setParams(HashMap<?, ?> params) {
        this.params = params;
    }

    public String getClazzMapping() {
        return clazzMapping;
    }

    public void setClazzMapping(String clazzMapping) {
        this.clazzMapping = clazzMapping;
    }
}
