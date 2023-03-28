package com.github.xiaour.easyexport.constants;

/**
 * @author zhangtao
 *  导出的常量以及相关标识符
 * create by 2021/7/8 16:39
 */
public class ExportConstant {

    private ExportConstant() {
    }

    public static final String PROJECT = "EasyExport";
    /**
     * 单个导出模块类分隔符
     */
    public static final String EXPORT_CLASS_SPLIT_TAG ="@";

    /**
     * 分组导出模块类分隔符
     */
    public static final String EXPORT_GROUP_SPLIT_TAG ="#";

    /**
     * 最大进度
     */
    public static final Integer MAX_PROGRESS = 100;

    /**
     * EXCEL最大记录数1048576
     * */
    public static final int MAX_SIZE = 1048576;

    /**
     * 文件后缀名
     */
    public static final String EXPORT_FILE_SUFFIX = ".xlsx";
    public static final String ZIP_EXPORT_FILE_SUFFIX = ".zip";

    /**
     * 默认的获取总数的方法名
     */
    public static final String METHOD_TOTAL = "getTotal";
    /**
     * 默认的获取列表数据的方法名
     */
    public static final String METHOD_LIST = "getData";

}
