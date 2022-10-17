package com.troublemaker.clockin.controller;

import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.execute.DoClockInTask;
import com.troublemaker.clockin.service.CommonService;
import com.troublemaker.utils.mailutils.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.controller
 * @Author: troublemaker
 * @CreateTime: 2022-10-11  01:12
 * @Version: 1.0
 */
@CrossOrigin
@RestController
@Slf4j
public class ClockinController {
    @Autowired
    private CommonService commonService;
    @Autowired
    private DoClockInTask doClockInTask;

    @Autowired
    private SendMail sendMail;

    @GetMapping("/clock")
    public ResultResponse clock() {
        if (commonService.doClock(doClockInTask, sendMail)) {
            return ResultResponse.success("打卡成功");
        } else {
            return ResultResponse.error("打卡失败，请重试");
        }
    }
}

