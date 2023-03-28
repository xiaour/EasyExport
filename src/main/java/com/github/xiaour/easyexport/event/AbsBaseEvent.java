package com.github.xiaour.easyexport.event;

import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.Map;

/**
 * AbsBaseEvent
 *
 * @version 1.1.0
 * @author zhang.tao
 * create by 2020/12/10 11:32
 *   事件基础类,禁止直接使用
 */
public abstract class AbsBaseEvent extends ApplicationEvent implements Serializable {

    private Map<String, Object> threadVar;

    private boolean sync = false;

    protected AbsBaseEvent(Object source) {
        super(source);
    }

    public Map<String, Object> getThreadVar() {
        return this.threadVar;
    }

    public void setThreadVar(Map<String, Object> threadVar) {
        this.threadVar = threadVar;
    }

    /**
     * 预留：暂时不启用
     * @return boolean
     */
    public boolean isSync() {
        return this.sync;
    }

    public void sync() {
        this.sync = true;
    }

}
