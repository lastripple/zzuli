package com.troublemaker.clockin.service;

import com.troublemaker.clockin.execute.DoClockInTask;
import com.troublemaker.utils.mailutils.SendMail;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service
 * @Author: troublemaker
 * @CreateTime: 2022-10-11  08:08
 * @Description: TODO
 * @Version: 1.0
 */
public interface CommonService {
    boolean doClock(DoClockInTask doClockInTask, SendMail sendMail);
}

