package com.github.xiaour.easyexport.event;

/**
 * @Author zhangtao
 * @Description 导出的事件
 * @Date 2022/7/22 16:36
 */
public class ExportEvent extends AbsBaseEvent {

    /**
     * 任务ID
     */
    private String  taskId;

    /**
     * 进度
     */
    private Integer process;//更新进度

    /**
     * 任务附加数据
     */
    private String taskData;

    /**
     * 是否执行完成
     */
    private boolean success;

    public ExportEvent(Object source, String taskId, Integer process, String taskData) {
        super(source);
        this.taskId = taskId;
        this.process = process;
        this.taskData = taskData;
        if(process.equals(100)){
            success = true;
        }
    }

    public ExportEvent(Object source, String taskId, Integer process) {
        super(source);
        this.taskId = taskId;
        this.process = process;
        if(process.equals(100)){
            success = true;
        }
    }

    public String getTaskId() {
        return taskId;
    }

    public Integer getProcess() {
        return process;
    }


    public String getTaskData() {
        return taskData;
    }

    public boolean success() {
        return success;
    }
}
