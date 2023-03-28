package com.github.xiaour.easyexport.handler;

/**
 * @author zhangtao
 *  上传接口
 * create by 2022/3/30 下午5:49
 */
public interface UploadCallback<T> {
        /**
         * 上传文件
         * @return 返回文件名
         */
        String upload();
}
