package com.troublemaker.order.excute;

import com.troublemaker.order.config.YamlOrderDataConfiguration;
import com.troublemaker.order.entity.Booker;
import com.troublemaker.order.entity.FieldInfo;
import com.troublemaker.order.service.FieldSelectionService;
import com.troublemaker.order.thread.OrderTask;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author Troublemaker
 * @date 2022- 04 30 22:57
 */
@Component
public class DoOrderTask {
    private final FieldSelectionService selectionService;
    private final HttpClientBuilder clientBuilder;
    private final List<FieldInfo> fieldInfos;
    private final ThreadPoolExecutor poolExecutor;
    private final YamlOrderDataConfiguration orderDataConfiguration;

    @Autowired
    public DoOrderTask(FieldSelectionService selectionService, HttpClientBuilder clientBuilder, List<FieldInfo> fieldInfos, ThreadPoolExecutor poolExecutor, YamlOrderDataConfiguration orderDataConfiguration) {
        this.selectionService = selectionService;
        this.clientBuilder = clientBuilder;
        this.fieldInfos = fieldInfos;
        this.poolExecutor = poolExecutor;
        this.orderDataConfiguration = orderDataConfiguration;
    }

    public void start() {
        List<Booker> bookers = selectionService.getBookers();
        int min = Math.min(bookers.size(), fieldInfos.size());
        CountDownLatch countDownLatch = new CountDownLatch(min);
        for (int i = 0; i < min; i++) {
            Booker booker = bookers.get(i);
            FieldInfo fieldInfo = fieldInfos.get(i);
            poolExecutor.execute(new OrderTask(booker, fieldInfo, orderDataConfiguration.getOrder(), clientBuilder, countDownLatch, selectionService));
        }

        // 等待子线程任务完成
        try {
            countDownLatch.await();
        } catch (Exception ignored) {

        }
    }
}
