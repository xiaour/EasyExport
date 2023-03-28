package com.github.xiaour.easyexport.handler;

/**
 * @author zhangtao
 *  初始化Excel执行
 * create by 2022/7/29 16:41
 */
public interface ModelHandler<T> {

    /**
     * 执行相应的接口
     * @return 返回文件名
     */
    T initModel();
}
