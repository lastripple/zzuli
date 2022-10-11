package com.troublemaker.clockin.service.impl;

import com.troublemaker.clockin.execute.DoClockInTask;
import com.troublemaker.clockin.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service.impl
 * @Author: troublemaker
 * @CreateTime: 2022-10-11  08:09
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Service
public class CommonServiceImpl implements CommonService {
    @Override
    public boolean doClock(DoClockInTask doClockInTask) {
        log.info("-----------------打卡启动-------------------");
        int count = 0;
        while (true) {
            count++;
            log.info("-----------------第" + count + "轮打卡-------------------");
            if (doClockInTask.start()) {
                log.info("-----------------打卡成功-------------------");
                log.info("-----------------打卡完成-------------------");
                return true;
            }
            if (count == 5) {
                log.info("-----------------已重试5次, 打卡失败-------------------");
                log.info("-----------------打卡完成-------------------");
                return false;
            }
        }
    }
}
