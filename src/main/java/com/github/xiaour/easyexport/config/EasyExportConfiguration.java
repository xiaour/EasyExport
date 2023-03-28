package com.github.xiaour.easyexport.config;

import com.github.xiaour.easyexport.EasyExportProvider;
import com.github.xiaour.easyexport.constants.ExportConstant;
import com.github.xiaour.easyexport.context.AppContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangtao
 *  导出组件配置类.
 * create by 2022/1/21 下午5:06
 */

@Slf4j
@Configuration
@EnableConfigurationProperties({EasyExportProperties.class})
@ConditionalOnProperty(name = {"bsf.com.github.xiaour.easyexport.enabled"}, havingValue = "true")
public class EasyExportConfiguration implements InitializingBean {

    private AppContextFactory appContextFactory;

    @Bean
    @ConditionalOnMissingBean
    public AppContextFactory initAppContext(){
        AppContextFactory context = new AppContextFactory();
        appContextFactory = context;
        log.info("EasyExport AppContextFactory initialized.");
        return  appContextFactory;
    }


    @Bean
    @DependsOn("initAppContext")
    public EasyExportProvider initEasyExportBean(EasyExportProperties easyExportProperties){
        Lock lock = new ReentrantLock();
        EasyExportProvider executor = new EasyExportProvider(appContextFactory,easyExportProperties,lock);
        log.info("EasyExportProvider initialized.");
        return executor;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("EasyExport started.");
    }
}
