package com.troublemaker.clockin;

import com.troublemaker.clockin.execute.DoClockInTask;
import com.troublemaker.clockin.service.CommonService;
import com.troublemaker.utils.mailutils.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author Troublemaker
 * @date 2022- 04 28 23:05
 */
@SpringBootTest
@Slf4j
public class ClockInApplicationTests {

    @Autowired
    private CommonService commonService;
    @Autowired
    private DoClockInTask doClockInTask;

    @Autowired
    private SendMail sendMail;


    @Test
    void contextLoads() {
        commonService.doClock(doClockInTask, sendMail);
    }
}
