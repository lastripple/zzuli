package com.troublemaker.clockin;

import com.troublemaker.clockin.execute.DoClockInTask;
import com.troublemaker.clockin.service.CommonService;
import com.troublemaker.clockin.service.LoginService;
import com.troublemaker.utils.mailutils.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @author Troublemaker
 * @date 2022- 04 29 10:08
 */
@Slf4j
@Component
public class ClockInRun {

    private final CommonService commonService;

    private final DoClockInTask doClockInTask;
    private final LoginService loginService;

    private final SendMail sendMail;
    public static long startTime;

    @Autowired
    public ClockInRun(CommonService commonService, DoClockInTask doClockInTask, LoginService loginService, SendMail sendMail) {
        this.commonService = commonService;
        this.doClockInTask = doClockInTask;
        this.loginService = loginService;
        this.sendMail = sendMail;
    }

    @Scheduled(cron = "${data.run.cron}")
    public void doClockIn() {
        commonService.doClock(doClockInTask, sendMail);
        loginService.cleanClient();
    }

}
