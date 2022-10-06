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
    private static final String VPN_URL = "https://webvpn.zzuli.edu.cn/login?cas_login=true";
    private static final String URL_PREFIX = "https://webvpn.zzuli.edu.cn/http/";
    private static final String LOGIN_URL = "/cas/login?service=https://webvpn.zzuli.edu.cn/login?cas_login=true";
    private static final String HOME_URL = "/User/UserChoose?LoginType=1";
    private static final String Order_Url = "/Field/OrderField?vpn-12-o1-cgyy.zzuli.edu.cn&dateadd=0&VenueNo=001&checkdata=";
    private static final String Submit_PREFIX = "/Field/CardPay?" +
            "vpn-12-o1-cgyy.zzuli.edu.cn" +
            "&PayNo=02" +
            "&Money=0" +
            "&CardMoney=1" +
            "&Count=0.00" +
            "&MemberNo=" +
            "&BillType=100" +
            "&Password=" +
            "&IsCheckPassword=0" +
            "&VenueNo=001" +
            "&PayDiscount=100" +
            "&IsUseMemberType=1" +
            "&EWMNum=1";


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
            String URL_Host;
            // 获取VPN HOST
            client = clientBuilder.build();
            String host = service.getHost(client, VPN_URL);
            URL_Host = URL_PREFIX + host;

            // 登录认证
            String lt = service.getLt(client, URL_Host + LOGIN_URL);
            service.login(client, URL_Host + LOGIN_URL, service.loginMap(booker, lt));

            // 获取OrderHost
            host = service.getOrderHost(client);
            URL_Host = URL_PREFIX + host;

            // 获取cookie
            service.getHomePage(client, URL_Host + HOME_URL);

            // 对预定场所进行处理
            String orderFieldJson = service.orderInvariableField(fieldInfo);

            // String url = "http://cgyy.zzuli.edu.cn/Field/OrderField?dateadd=0&VenueNo=001&checkdata=" + checkData;
            // /Field/OrderField?vpn-12-o1-cgyy.zzuli.edu.cn&dateadd=0&VenueNo=001&checkdata=
            // 预约
            String oId = service.order(client, URL_Host + Order_Url + orderFieldJson);

            // 提交
            // 重复提交，避免sql死锁导致资源释放
            int count = orderData.getRetryCount();
            int intervalTime = orderData.getIntervalTime();
            String message = null;
            for (int i = 0; i < count; i++) {
                String submitUrl = URL_PREFIX + Submit_PREFIX +
                        "&CardNo=" + booker.getUsername() +
                        "&OID=" + oId +
                        "&_=" + System.currentTimeMillis();
                message = service.subMit(client, submitUrl);
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
