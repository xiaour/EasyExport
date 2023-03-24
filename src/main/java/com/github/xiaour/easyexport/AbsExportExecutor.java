package com.github.xiaour.easyexport;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.xiaour.easyexport.builder.EasyExportBuilder;
import com.github.xiaour.easyexport.builder.ExportContext;
import com.github.xiaour.easyexport.config.EasyExportProperties;
import com.github.xiaour.easyexport.constants.ExportConstant;
import com.github.xiaour.easyexport.context.AppContextFactory;
import com.github.xiaour.easyexport.event.ExportEvent;
import com.github.xiaour.easyexport.exception.EasyExportException;
import com.github.xiaour.easyexport.handler.UploadCallback;
import com.github.xiaour.easyexport.utils.FileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @Author zhangtao
 * @Description 统一导出方案
 * @Date 2022/3/11 下午1:58
 */
@Slf4j
public abstract class AbsExportExecutor {


    private AppContextFactory appContextFactory;

    private EasyExportProperties properties;

    private EasyExportBuilder easyExportBuilder;

    private Lock lock;


    public AbsExportExecutor() {

    }

    protected AbsExportExecutor(AppContextFactory appContext, EasyExportProperties properties,Lock lock) {
        if(properties == null){
            throw new EasyExportException("未正确初始化EasyExport，请配置：csx.data.lab.export.enabled:true");
        }
        if(lock == null){
            throw new EasyExportException("未正确初始化Lock，当前线程无法继续执行！");
        }
        log.debug("-------------EasyExport 正在初始化配置------------");
        log.debug("EasyExport 生成文件存储路径："+properties.getFilePath());
        log.debug("EasyExport 完成后删除原始文件："+properties.isDelete());
        log.debug("EasyExport 页码字段："+properties.getPageNumberField());
        log.debug("EasyExport 每页记录数字段："+properties.getPageSizeField());
        log.debug("EasyExport 每页记默认录数："+properties.getPageSize());
        log.debug("-------------EasyExport 配置初始化完成------------");
        this.appContextFactory = appContext;
        this.properties = properties;
        this.lock = lock;

    }

    public AppContextFactory appContextFactory() {
        return appContextFactory;
    }

    public AbsExportExecutor executorExportBuilder(final EasyExportBuilder easyExportBuilder) {
        this.easyExportBuilder = easyExportBuilder;
        Assert.notNull(easyExportBuilder,"executorExportBuilder is not null");
        return this;
    }

    public EasyExportProperties getProperties() {
        Assert.notNull(properties,"easyExportProperties is not null");
        return this.properties;
    }

    public EasyExportBuilder getEasyExportBuilder() {
        return easyExportBuilder;
    }

    /**
     * 获取总条数，用于计算进度条
     * @param exportContext
     * @return
     */
    public abstract Long getTotal(ExportContext exportContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     * 获取下一页,分页获取并返回
     * @param exportContext
     * @param pageNum
     * @return
     */
    public abstract List<?> getNextPage(ExportContext exportContext,int pageNum) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;


    /**
     * 普通导出
     * @param easyExportBuilder
     * @return
     */
    @SneakyThrows
    public AbsExportExecutor doExport(EasyExportBuilder easyExportBuilder){
        //如果获取到锁，就直接执行，注意此锁只能锁30分钟，如果导出超长时间未执行完成，会自动释放。
        if(lock.tryLock(30,TimeUnit.MINUTES)){
            this.easyExportBuilder = easyExportBuilder;
            try {
                return exportProcess();
            } catch (Exception e) {
                log.error("导出失败：锁已释放{}",e);
                lock.unlock();
                return this;
            }
        }else{
            //不断重试
            return doExport(easyExportBuilder);
        }
    }

    /**
     * 上传文件
     * @param uploadCallBack
     * @return
     */
    public AbsExportExecutor upload(UploadCallback uploadCallBack){
        try {
            String remoteFileName = uploadCallBack.upload();
            Assert.notNull(remoteFileName,"remoteFileName should not be null");
            this.easyExportBuilder.getExportContext().setRemoteUrl(remoteFileName);
        } catch (Exception e) {
            log.error("导出失败：锁已释放{}",e);
            lock.unlock();
        }
        return  this;
    }


    /**
     * 普通导出处理
     */
    private AbsExportExecutor exportProcess(){
        ExportContext context = this.getEasyExportBuilder().getExportContext();
        String taskId = context.getTaskId();
        int progress = 5;
        appContextFactory.publish(new ExportEvent(this,context.getTaskId(),progress));
        ///创建存入七牛云后的文件名称
        String fileName = getFileName(false);
        log.info("任务ID：{}，文件名：{}，准备查询数据写入excel...", taskId, fileName);
        try {
            //创建Excel
            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(fileName);

            addBuildConvert(excelWriterBuilder);

            ExcelWriter excelWriter = excelWriterBuilder.build();
            log.info("任务ID：{}，文件名：{}", taskId, fileName);
            //写入数据
            WriteSheet writeSheet = null;
            //分页当前页
            int pageNum = 1;
            //sheet当前页
            int sheetNum = 1;
            //当前sheet页有多少数据
            int count = 0;
            //每次查询sql总数
            boolean flag = true;
            // 如果headClass为null，则使用getHeader方法定义的动态表头
            Assert.notNull(context.getExcelHeaderClazz(),"没有传入导出的模板类，导出中断！");

            writeSheet = EasyExcel.writerSheet(sheetNum, "第" + sheetNum + "页").head(context.getExcelHeaderClazz()).build();

            List<?> list = new ArrayList<>();

            Long total = getTotal(context);

            Long part = total == 0 ? 1 : (total % properties.getPageSize() == 0 ? total / properties.getPageSize() : total / properties.getPageSize() + 1);
            log.info("任务ID：{}，文件名：{}，当前文件需写入{}次", taskId, fileName, part);
            int eachProgress = 90;
            int progressSize = (int) (eachProgress % part == 0 ? eachProgress / part : eachProgress / part + 1);
            while (flag) {
                if (total > 0) {
                    if(pageNum > (total/ properties.getPageSize()+1)){
                        throw new EasyExportException("导出发生异常：页码大于实际的数据量，可能是因为多线程分页问题！");
                    }
                    list = getNextPage(context, pageNum);

                    log.info("任务ID：{}，文件名：{}，第{}次获取数据，当前数据共{}行",
                            taskId, fileName, pageNum, list.size());
                }
                count = count + list.size();
                //当前sheet加本次查询的总数大于传入的每页sheet数时创建sheet
                if (count > properties.getSheetSize()) {
                    count = list.size();
                    sheetNum++;
                    //创建sheet
                    writeSheet = EasyExcel.writerSheet(sheetNum, "第" + sheetNum + "页").build();
                }
                //当本次查询数据小于传入的sql总数时，取消循环。
                if (list.size() < properties.getPageSize()) {
                    flag = false;
                }
                //写入数据
                excelWriter.write(list, writeSheet);
                //释放资源
                list.clear();
                pageNum++;
                //每次循环更新进度，否则超过15分钟(任务中心固定值)，没有响应此值，任务会自动取消
                progress = progress + progressSize;
                //防止进度等用100,如等于100则任务结束
                eachProgress = 95;

                appContextFactory.publish(new ExportEvent(this,context.getTaskId(),progress < eachProgress?progress:eachProgress));

                log.info("任务ID：{}，文件名：{}，第{}个sheet，第{}次写入{}行数据，当前任务进度：{}", taskId, fileName, sheetNum, pageNum-1, list.size(),progress);
            }
            //关闭流
            excelWriter.finish();
            appContextFactory.publish(new ExportEvent(this,context.getTaskId(),97));
            log.info("最后一步：任务ID：{}，文件名：{}，一共写入{}次，当前任务进度：{} ", taskId, fileName, pageNum-1, progress);
        }catch(Exception e){
            e.printStackTrace();
            log.error("导出失败,任务ID：{},任务名称：{}，异常信息：{}", taskId,context.getTaskName(),e);
        }

        File file = new File(fileName);

        Assert.isTrue(file.exists(),"任务ID："+ taskId+"，总文件不存在");

        log.info("任务ID：{}，任务名称：{}，总文件{}生成成功，文件大小{}B", taskId, context.getTaskName(), fileName, FileUtils.getFileSize(fileName));

        log.info("执行完成！任务ID：#{}，文件路径：#{}", taskId, fileName);

        context.setLocalFile(fileName);
        return  this;
    }

    protected void addBuildConvert(ExcelWriterBuilder excelWriterBuilder){
        if(this.easyExportBuilder.getConverter()!= null){
            excelWriterBuilder.registerConverter(this.easyExportBuilder.getConverter());
        }
    }

    /**
     * 删除生成的本地文件，返回远程文件链接
     * @return
     */
    public String finish(){
        try {
            ExportContext context = easyExportBuilder.getExportContext();
            if(context!= null){
                context.taskAttach(context.getRemoteUrl());
                appContextFactory.publish(new ExportEvent(this,context.getTaskId(),100,context.getRemoteUrl()));
                if(easyExportBuilder.isDeleteFile()&&context.getLocalFile()!= null){
                    FileUtils.deleteFile(context.getLocalFile());
                    log.info("任务ID：{}，任务名称：{}，总文件{}删除成功",context.getTaskId(),context.getTaskName(),context.getLocalFile());
                }
                return context.getRemoteUrl();
            }
        } finally {
            lock.unlock();
            log.info("全局锁已经释放");
        }
        return null;
    }


    public String getFileName(boolean isZip){
        ExportContext context = easyExportBuilder.getExportContext();
        if(StringUtils.isEmpty(properties.getFilePath())){
            properties.setFilePath(this.getClass().getResource("/").getPath());
        }
        String currentFilePath = properties.getFilePath()+ DateUtils.format(new Date(),"yyyy-MM-dd");
        FileUtils.mkdir(new File(currentFilePath));
        if(isZip){
            return currentFilePath+"/"+context.getTaskName()+context.getTaskId()+"-"+System.currentTimeMillis() + ExportConstant.ZIP_EXPORT_FILE_SUFFIX;
        }
        return currentFilePath+"/"+context.getTaskName()+context.getTaskId()+"-"+System.currentTimeMillis() + ExportConstant.EXPORT_FILE_SUFFIX;
    }


}
