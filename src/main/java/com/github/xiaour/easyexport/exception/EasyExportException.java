package com.github.xiaour.easyexport.exception;

/**
 * @Author zhangtao
 * @Description this is exception
 * @Date 2021/9/13 下午1:49
 */
public class EasyExportException extends RuntimeException{

    public EasyExportException(String message) {
        super(message);
    }

    public EasyExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
