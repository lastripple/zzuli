package com.troublemaker.clockin.execute;

import com.troublemaker.clockin.entity.User;
import com.troublemaker.clockin.service.*;
import com.troublemaker.clockin.thread.ClockInTask;
import com.troublemaker.utils.mailutils.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;


/**
 * @author Troublemaker
 * @date 2022- 04 30 22:16
 */
@Component
@Slf4j
public class DoClockInTask {

    private final ClockInService clockInService;
    private final LoginService loginService;

    private final UserService userService;

    private final HomeService homeService;

    private final SchoolService schoolService;

    private final SendMail sendMail;
    private final HttpClientBuilder clientBuilder;
    private final ThreadPoolExecutor poolExecutor;

    @Autowired
    public DoClockInTask(ClockInService clockInService, LoginService loginService, UserService userService, HomeService homeService, SchoolService schoolService, SendMail sendMail, HttpClientBuilder clientBuilder, ThreadPoolExecutor poolExecutor) {
        this.clockInService = clockInService;
        this.loginService = loginService;
        this.userService = userService;
        this.homeService = homeService;
        this.schoolService = schoolService;
        this.sendMail = sendMail;
        this.clientBuilder = clientBuilder;
        this.poolExecutor = poolExecutor;
    }

    public boolean start() {
        // 获取 clock_type != 2, clock_status = 0;
        List<User> users = userService.getUnClockUsers();
        CountDownLatch countDownLatch = new CountDownLatch(users.size());

        for (User user : users) {
            // 自定义全局线程池
            // CallerRunsPolicy()策略, 即调用者(main)执行该任务
            poolExecutor.execute(new ClockInTask(user, sendMail, clockInService, loginService, userService, homeService, schoolService, clientBuilder, countDownLatch));
        }

        // 等待子线程任务完成
        try {
            countDownLatch.await();

        } catch (Exception ignored) {
        }

        // 获取 clock_type != 2, clock_status = 0;
        boolean empty = userService.getUnClockUsers().isEmpty();
        if (empty) {
            userService.reSetClockStatus();
            return true;
        } else {
            return false;
        }
    }

}
