package com.troublemaker.clockin.thread;

import com.troublemaker.clockin.entity.*;
import com.troublemaker.clockin.service.*;
import com.troublemaker.utils.mailutils.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


import java.util.concurrent.CountDownLatch;

import static com.troublemaker.utils.httputils.HttpClientUtils.*;

/**
 * @author Troublemaker
 * @date 2022- 04 30 22:06
 */
@Slf4j
public class ClockInTask implements Runnable {
    private final User user;
    private final SendMail sendMail;
    private final ClockInService clockInService;

    private final LoginService loginService;

    private final UserService userService;

    private final HomeService homeService;

    private final SchoolService schoolService;

    private final HttpClientBuilder clientBuilder;
    private final CountDownLatch countDownLatch;

    private CloseableHttpClient client = null;
    private static final String SUCCESS = "{\"code\":0,\"message\":\"ok\"}";
    private String clockInfo = null;
    private static final String LOGIN_URL = "http://kys.zzuli.edu.cn/cas/login";
    private String codeUrl = "https://msg.zzuli.edu.cn/xsc/week?spm=";
    private static final String ADD_URL = "https://msg.zzuli.edu.cn/xsc/add";
    private String userInfoUrl = "https://msg.zzuli.edu.cn/xsc/get_user_info?wj_type=";
//    private static final String HISTORY_URL = "https://msg.zzuli.edu.cn/xsc/log?type=0";

    public ClockInTask(User user, SendMail sendMail, ClockInService clockInService, LoginService loginService, UserService userService, HomeService homeService, SchoolService schoolService, HttpClientBuilder clientBuilder, CountDownLatch countDownLatch) {
        this.user = user;
        this.sendMail = sendMail;
        this.clockInService = clockInService;
        this.loginService = loginService;
        this.userService = userService;
        this.homeService = homeService;
        this.schoolService = schoolService;
        this.clientBuilder = clientBuilder;
        this.countDownLatch = countDownLatch;
        codeUrl += user.getClockType();
        userInfoUrl += user.getClockType();
    }

    @Override
    public void run() {
        try {
            client = clientBuilder.build();
            // ??????
            String lt = loginService.getLt(client, LOGIN_URL);
            loginService.login(client, LOGIN_URL, loginService.loginMap(user, lt));

            // ????????????code????????????code=8055141d21s21sd411dd63
            String link = clockInService.getCodeLink(client, codeUrl);
            // ??????TOKEN
            String token = clockInService.getToken(client, link);
            Header header = getHeader("X-XSRF-TOKEN", token);

            // ???code?????????url???
            link = link.substring(link.indexOf("code"));
            link = "&" + link.substring(0, link.indexOf("&"));
            userInfoUrl += link;

            Object inputData;
            if (1 == user.getClockType()) {
                // ???????????????
                SchoolInputData schoolInputData = clockInService.getSchoolInfoFromServer(client, userInfoUrl);
                // ???????????????
                School school = schoolService.getSchoolByUserId(user.getUid());
                // ????????????
                inputData = clockInService.SchoolFinalData(schoolInputData, school);
            } else {
                // ???????????????
                HomeInputData homeInputData = clockInService.getHomeInfoFromServer(client, userInfoUrl);
                // ???????????????
                Home home = homeService.getHomeByUserId(user.getUid());
                // ????????????
                inputData = clockInService.HomeFinalData(homeInputData, home);
            }

            // ??????????????????
            int count = 0;
            while (true) {
                count++;
                clockInfo = clockInService.submitData(client, ADD_URL, inputData, header);
                if (SUCCESS.equals(clockInfo)) {
                    log.info(user.getUsername() + " " + clockInfo);
                    // ??????????????????????????????????????? 1, ???????????????
                    userService.changeClockStatus(user);
//                    sendMail.sendSimpleMail(user.getEmail(), "?????????????????????????????????,????????????????????????");
                    break;
                }
                if (count == 3) {
                    log.info(user.getUsername() + " " + clockInfo);
//                    sendMail.sendSimpleMail(user.getEmail(), "????????????????????????????,????????????????,???????????????????");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("??????: " + e);
            if (!SUCCESS.equals(clockInfo)) {
//                sendMail.sendSimpleMail(user.getEmail(), "????????????????????????????,????????????????,???????????????????");
            }
        } finally {
            countDownLatch.countDown();
            clientClose(client);
        }
    }
}

