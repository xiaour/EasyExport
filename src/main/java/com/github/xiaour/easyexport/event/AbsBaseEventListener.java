package com.github.xiaour.easyexport.event;

import com.github.xiaour.easyexport.context.AppContextFactory;
import com.github.xiaour.easyexport.exception.EasyExportException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AbsCommonEventListener
 *
 * @version 1.1.0
 * @author zhang.tao
 * create by 2020/12/10 13:46
 *
 */
@Slf4j
@Component
public abstract class AbsBaseEventListener<E extends AbsBaseEvent> implements ApplicationListener<E> {


    @Async
    public void onApplicationEvent(E event) {
        Map<String, Object> threadVar = event.getThreadVar();
        if (threadVar != null && threadVar.size() > 0) {
            AppContextFactory.putAllThreadContext(threadVar);
        }

        try {
            this.doEvent(event);
        } catch (Exception e) {
            log.error("事件执行发生异常...",e);
            throw new EasyExportException(e.getMessage());
        }finally {
            //异步需要清理线程变量
            if (!event.isSync()) {
                AppContextFactory.clearThreadContext();
            }
        }

    }

    public abstract void doEvent(E event) throws EasyExportException;
}
