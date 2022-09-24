package com.troublemaker.order.thread;

import com.troublemaker.order.entity.Booker;
import com.troublemaker.order.entity.FieldInfo;
import com.troublemaker.order.entity.YamlOrderData;
import com.troublemaker.order.service.FieldSelectionService;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


/**
 * @author Troublemaker
 * @date 2022- 04 30 22:51
 */
@Slf4j
public class OrderTask implements Runnable {
    private final Booker booker;
    private final FieldInfo fieldInfo;

    private final YamlOrderData orderData;
    private final HttpClientBuilder clientBuilder;
    private final CountDownLatch countDownLatch;
    private CloseableHttpClient client = null;

    private final FieldSelectionService service;
    private static final String LOGIN_URL = "http://kys.zzuli.edu.cn/cas/login";
    private static final String HOME_URL = "http://cgyy.zzuli.edu.cn/User/UserChoose?LoginType=1";

    public OrderTask(Booker booker, FieldInfo fieldInfo, YamlOrderData orderData, HttpClientBuilder clientBuilder, CountDownLatch countDownLatch, FieldSelectionService service) {
        this.booker = booker;
        this.fieldInfo = fieldInfo;
        this.orderData = orderData;
        this.clientBuilder = clientBuilder;
        this.countDownLatch = countDownLatch;
        this.service = service;
    }

    @Override
    public void run() {
        try {
            client = clientBuilder.build();
            // 登录
            String lt = service.getLt(client, LOGIN_URL);
            service.login(client, LOGIN_URL, service.loginMap(booker, lt));

            // 获取cookie
            service.getHomePage(client, HOME_URL);

            // 对预定场所进行处理
            String orderFieldJson = service.orderInvariableField(fieldInfo);

            // 预约
            String oId = service.order(client, orderFieldJson);

            // 提交
            // 重复提交，避免sql死锁导致资源释放
            int count = orderData.getRetryCount();
            int intervalTime = orderData.getIntervalTime();
            String message = null;
            for (int i = 0; i < count; i++) {
                message = service.subMit(client, booker.getUsername(), oId);
                if ("预订成功！".equals(message)) {
                    break;
                } else {
                    Thread.sleep(intervalTime);
                }
            }
            log.info(booker.getUsername() + " " + fieldInfo.getFieldName() + " 预约状态: " + message);
        } catch (Exception e) {
            log.error(booker.getUsername() + " 预约失败 " + fieldInfo.getFieldName() + " 异常信息: " + e);
        } finally {
            countDownLatch.countDown();
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    log.error("异常: " + e);
                }
            }
        }
    }
}
