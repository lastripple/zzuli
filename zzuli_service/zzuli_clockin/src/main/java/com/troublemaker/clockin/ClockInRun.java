package com.troublemaker.clockin;

import com.troublemaker.clockin.execute.DoClockInTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.Enumeration;

import static com.troublemaker.utils.httputils.HttpClientUtils.clientClose;

/**
 * @author Troublemaker
 * @date 2022- 04 29 10:08
 */
@Slf4j
@Component
public class ClockInRun {

    private DoClockInTask doClockInTask;

    private ServletContext servletContext;

    public static long startTime;

    @Autowired
    public void setDoClockInTask(DoClockInTask doClockInTask, ServletContext servletContext) {
        this.doClockInTask = doClockInTask;
        this.servletContext = servletContext;
    }

    @Scheduled(cron = "${data.run.cron}")
    public void doClockIn() {
        log.info("-----------------打卡启动-------------------");
        startTime = System.currentTimeMillis();
        doClockInTask.start();
        log.info("-----------------打卡完成-------------------");
        log.info("-----------------清理启动-------------------");
        int count = 0;
        Enumeration<String> e = servletContext.getAttributeNames();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            Object value = servletContext.getAttribute(key);
            servletContext.removeAttribute(key);
            if (value instanceof CloseableHttpClient) {
                count++;
                CloseableHttpClient client = (CloseableHttpClient) value;
                clientClose(client);
            }
        }
        log.info("总数: " + count);
        log.info("-----------------清理完成-------------------");
    }

}
