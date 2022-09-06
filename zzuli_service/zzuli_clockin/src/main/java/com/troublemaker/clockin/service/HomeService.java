package com.troublemaker.clockin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.troublemaker.clockin.entity.Home;
import com.troublemaker.clockin.entity.HomeInputData;
import com.troublemaker.clockin.entity.ResultResponse;

import javax.servlet.ServletContext;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service
 * @Author: troublemaker
 * @CreateTime: 2022-08-28  00:43
 * @Description: TODO
 * @Version: 1.0
 */
public interface HomeService extends IService<Home> {
    Home getHomeByUserId(String uid);

    HomeInputData getHomeClockInfo(String token, ServletContext servletContext);

    ResultResponse addHomeClockInfo(String token, Home home);
}
