package com.troublemaker.clockin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.troublemaker.clockin.config.JWTTokenConfiguration;
import com.troublemaker.clockin.entity.Home;
import com.troublemaker.clockin.entity.HomeInputData;
import com.troublemaker.clockin.entity.ResultEnum;
import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.mapper.HomeMapper;
import com.troublemaker.clockin.service.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service.impl
 * @Author: troublemaker
 * @CreateTime: 2022-08-28  00:45
 * @Version: 1.0
 */
@Service
public class HomeServiceImpl extends ServiceImpl<HomeMapper, Home> implements HomeService {

    private final JWTTokenConfiguration jwtToken;
    private final ClockInService clockInService;

    private final UserService userService;

    @Autowired
    public HomeServiceImpl(JWTTokenConfiguration jwtToken, ClockInService clockInService, UserService userService) {
        this.jwtToken = jwtToken;
        this.clockInService = clockInService;
        this.userService = userService;
    }

    @Override
    public Home getHomeByUserId(String uid) {
        return baseMapper.selectById(uid);
    }

    @Override
    public HomeInputData getHomeClockInfo(String token, ServletContext servletContext) {
        String userID = jwtToken.verifyToken(token).get("userID").toString().replace("\"", "");
        String url = "https://msg.zzuli.edu.cn/xsc/week?spm=0";
        String infoUrl = "https://msg.zzuli.edu.cn/xsc/get_user_info?wj_type=0";
        CloseableHttpClient client = (CloseableHttpClient) servletContext.getAttribute(userService.getById(userID).getUsername());
        String link = clockInService.getCodeLink(client, url);
        link = link.substring(link.indexOf("code"));
        link = "&" + link.substring(0, link.indexOf("&"));
        infoUrl += link;
        HomeInputData homeInputData = clockInService.getHomeInfoFromServer(client, infoUrl);
        Home home = getHomeByUserId(userID);
        return clockInService.HomeFinalData(homeInputData, home);
    }

    @Override
    public ResultResponse addHomeClockInfo(String token, Home home) {
        String userID = jwtToken.verifyToken(token).get("userID").toString().replace("\"", "");
        home.setUid(userID);
        int i = baseMapper.insert(home);
        if (i > 0) {
            return ResultResponse.success();
        } else {
            return ResultResponse.error(ResultEnum.INTERNAL_SERVER_ERROR);
        }
    }
}

