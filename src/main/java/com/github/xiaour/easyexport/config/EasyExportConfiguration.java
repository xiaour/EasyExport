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

/**
 * @Author zhangtao
 * @Description 导出组件配置类.
 * @Date 2022/1/21 下午5:06
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
        LogUtils.info(EasyExportConfiguration.class, ExportConstant.PROJECT,"AppContextFactory initialized.");
        return  appContextFactory;
    }


    @Bean
    @DependsOn("initAppContext")
    public EasyExportProvider initEasyExportBean(EasyExportProperties easyExportProperties){
        EasyExportProvider executor = new EasyExportProvider(appContextFactory,easyExportProperties);
        LogUtils.info(EasyExportConfiguration.class,ExportConstant.PROJECT,"EasyExportProvider initialized.");
        return executor;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.info(EasyExportConfiguration.class,ExportConstant.PROJECT,"started.");
    }
}
