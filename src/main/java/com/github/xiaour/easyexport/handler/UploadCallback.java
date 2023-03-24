package com.github.xiaour.easyexport.handler;

/**
 * @Author zhangtao
 * @Description 上传接口
 * @Date 2022/3/30 下午5:49
 */
public interface UploadCallback<T> {
        /**
         * 上传文件
         * @return 返回文件名
         */
        String upload();
}
