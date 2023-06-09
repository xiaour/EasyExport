package com.github.xiaour.easyexport.event;

/**
 * CommonEvent
 * @version 1.1.0
 * @author zhang.tao
 * create by 2020/12/10 13:45
 *
 */
public class CommonEvent  extends AbsBaseEvent {

    private boolean ignore = Boolean.TRUE;//是否发送

    private String jsonStr;//具体业务内容JSON


    public CommonEvent(Object source) {
        super(source);
    }

    public CommonEvent(Object source,String jsonStr, boolean ignore) {
        super(source);
        this.jsonStr = jsonStr;
        this.ignore = ignore;
    }

    public Boolean getIgnore() {
        return ignore;
    }

    /**
     * 是否确认要发送事件
     * @return CommonEvent
     */
    public CommonEvent ignore() {
        this.ignore = true;
        return this;
    }

    public CommonEvent(Object source,String jsonStr) {
        super(source);
        this.jsonStr = jsonStr;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public CommonEvent setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
        return  this;
    }

}
