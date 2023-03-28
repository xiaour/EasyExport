package com.github.xiaour.easyexport.exception;

/**
 * @author zhangtao
 *  this is exception
 * create by 2021/9/13 下午1:49
 */
public class EasyExportException extends RuntimeException{

    public EasyExportException(String message) {
        super(message);
    }

    public EasyExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
