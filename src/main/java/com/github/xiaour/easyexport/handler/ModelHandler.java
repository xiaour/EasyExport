package com.github.xiaour.easyexport.handler;

/**
 * @Author zhangtao
 * @Description 初始化Excel执行
 * @Date 2022/7/29 16:41
 */
public interface ModelHandler<T> {

    /**
     * 执行相应的接口
     * @return 返回文件名
     */
    T initModel();
}
