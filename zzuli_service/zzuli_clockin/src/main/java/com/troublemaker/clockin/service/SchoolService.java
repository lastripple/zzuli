package com.troublemaker.clockin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.troublemaker.clockin.entity.*;

import javax.servlet.ServletContext;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service
 * @Author: troublemaker
 * @CreateTime: 2022-08-28  00:42
 * @Description: TODO
 * @Version: 1.0
 */
public interface SchoolService extends IService<School> {
    School getSchoolByUserId(String uid);

    SchoolInputData getSchoolClockInfo(String token, ServletContext servletContext);

    ResultResponse addSchoolClockInfo(String token, School school);
}

