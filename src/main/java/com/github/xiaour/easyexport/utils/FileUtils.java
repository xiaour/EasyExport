package com.github.xiaour.easyexport.utils;

import ch.qos.logback.core.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;

@Slf4j
public class FileUtils {
    private FileUtils() {
    }


    public static void deleteFile(String filePath) {
        try{
           Files.delete(new File(filePath).toPath());
        }catch(Exception e){
            log.error("", e);
        }
    }

    /**
     * 获取文件大小
     * @param filename
     * @return
     */
    public static long getFileSize(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            return -1;
        }
        return file.length();
    }

    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (false == dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

}
