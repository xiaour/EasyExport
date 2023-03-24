package com.github.xiaour.easyexport.builder;


import org.springframework.util.Assert;

/**
 * @Author zhangtao
 * @Description 导出上下文
 * @Date 2022/7/22 15:13
 */
public class ExportContext {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 执行任务参数
     */
    private ExportParam exportParam;

    /**
     * 总进度
     */
    private Integer totalProgress;

    /**
     * 当前进度
     */
    private Integer currProgress;

    /**
     * 附加数据
     */
    private String taskData;

    /**
     * 任务文件
     */
    private String taskAttach;


    /**
     * 导出表头实体类
     */
    private Class<?> excelHeaderClazz;

    /**
     * 当前用户信息
     */
    private Object currentUser;

    /**
     * 用户信息的类，直接从给currentUser赋值的时候获取
     */
    private Class<?> userClass;



    /**
     * 默认是com.yh.csx.business.api.entity.Page.getData(),如果不是请设置此方法
     */
    private String dataListMethod;

    /**
     * 默认是com.yh.csx.business.api.entity.Page.getTotal(),如果不是请设置此方法
     */
    private String dataTotalMethod;

    private String remoteUrl;
    //本地文件全路径
    private String localFile;


    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public String getDataListMethod() {
        return dataListMethod;
    }

    public String getDataTotalMethod() {
        return dataTotalMethod;
    }


    public ExportContext currentUser(final Object currentUser ,final Class<?> userClass) {
        this.userClass = userClass;
        this.currentUser = currentUser;
        Assert.notNull(userClass,"userClass should not be null");
        Assert.notNull(currentUser,"currentUser should not be null");
        return this;
    }

    /**
     * 如果没有使用BSF中的com.yh.csx.business.api.entity.Page请设置默认方法
     * @param dataTotalMethod  获取总记录数的方法，必须是无参的get方法
     * @param dataListMethod   获取数据列表的方法，必须是无参的get方法
     * @return
     */
    public ExportContext pageMethod(String dataTotalMethod ,String dataListMethod) {
        this.dataTotalMethod = dataTotalMethod;
        this.dataListMethod = dataListMethod;
        Assert.notNull(dataTotalMethod,"dataTotalMethod should not be null");
        Assert.notNull(dataListMethod,"dataListMethod should not be null");
        return this;
    }


    public ExportContext excelHeaderClazz(final Class<?> excelHeaderClazz) {
        this.excelHeaderClazz = excelHeaderClazz;
        Assert.notNull(excelHeaderClazz,"excelHeaderClazz should not be null");
        return this;
    }

    public Object getCurrentUser() {
        return currentUser;
    }

    public Class<?> getUserClass() {
        return userClass;
    }


    public Class<?> getExcelHeaderClazz() {
        return excelHeaderClazz;
    }

    /**
     * 首选构造方法
     * @param taskId
     * @param taskName
     * @param exportParam
     */
    public ExportContext(String taskId, String taskName, ExportParam exportParam) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.exportParam = exportParam;
    }

    public ExportContext(String taskId, String taskName, ExportParam exportParam, Integer totalProgress, Integer currProgress, String taskData, String taskAttach) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.exportParam = exportParam;
        this.totalProgress = totalProgress;
        this.currProgress = currProgress;
        this.taskData = taskData;
        this.taskAttach = taskAttach;
    }

    public ExportContext() {
    }


    public String getTaskId() {
        return taskId;
    }

    public ExportContext taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public ExportContext taskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public ExportContext exportParam(ExportParam exportParam) {
        this.exportParam = exportParam;
        return this;
    }

    public ExportContext currProgress(Integer currProgress) {
        this.currProgress = currProgress;
        return this;
    }

    public ExportContext totalProgress(Integer totalProgress) {
        this.totalProgress = totalProgress;
        return this;
    }

    public ExportContext taskData(String taskData) {
        this.taskData = taskData;
        return this;
    }

    public ExportContext taskAttach(String taskAttach) {
        this.taskAttach = taskAttach;
        return this;
    }

    public String getTaskName() {
        return taskName;
    }

    public ExportParam getExportParam() {
        return exportParam;
    }

    public Integer getTotalProgress() {
        return totalProgress;
    }

    public Integer getCurrProgress() {
        return currProgress;
    }

    public String getTaskData() {
        return taskData;
    }

    public String getTaskAttach() {
        return taskAttach;
    }

    public String getSimpleAttachName(){
        Assert.notNull(getTaskAttach(),"taskAttach should not be null");
        String originName = getTaskAttach().substring(getTaskAttach().lastIndexOf("/")+1);
        if(originName.contains(".")){
            return  getTaskName()+originName.hashCode()+"."+originName.split("\\.")[1];
        }
        return originName;
    }
}
