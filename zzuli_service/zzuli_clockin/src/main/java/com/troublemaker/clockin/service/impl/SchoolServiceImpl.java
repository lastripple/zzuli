package com.troublemaker.clockin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.troublemaker.clockin.config.JWTTokenConfiguration;
import com.troublemaker.clockin.entity.*;
import com.troublemaker.clockin.mapper.SchoolMapper;
import com.troublemaker.clockin.service.ClockInService;
import com.troublemaker.clockin.service.SchoolService;
import com.troublemaker.clockin.service.UserService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service
 * @Author: troublemaker
 * @CreateTime: 2022-08-28  00:46
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {
    private final JWTTokenConfiguration jwtToken;
    private final ClockInService clockInService;

    private final UserService userService;

    @Autowired
    public SchoolServiceImpl(JWTTokenConfiguration jwtToken, ClockInService clockInService, UserService userService) {
        this.jwtToken = jwtToken;
        this.clockInService = clockInService;
        this.userService = userService;
    }

    @Override
    public School getSchoolByUserId(String uid) {
        QueryWrapper<School> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public SchoolInputData getSchoolClockInfo(String token, ServletContext servletContext) {
        String userID = jwtToken.verifyToken(token).get("userID").toString().replace("\"", "");
        String url = "https://msg.zzuli.edu.cn/xsc/week?spm=1";
        String infoUrl = "https://msg.zzuli.edu.cn/xsc/get_user_info?wj_type=1";
        CloseableHttpClient client = (CloseableHttpClient) servletContext.getAttribute(userService.getById(userID).getUsername());
        String link = clockInService.getCodeLink(client, url);
        link = link.substring(link.indexOf("code"));
        link = "&" + link.substring(0, link.indexOf("&"));
        infoUrl += link;
        SchoolInputData schoolInputData = clockInService.getSchoolInfoFromServer(client, infoUrl);
        School school = getSchoolByUserId(userID);
        return clockInService.SchoolFinalData(schoolInputData, school);
    }

    @Override
    public ResultResponse addSchoolClockInfo(String token, School school) {
        String userID = jwtToken.verifyToken(token).get("userID").toString().replace("\"", "");
        school.setUid(userID);
        int i = baseMapper.insert(school);
        if (i > 0) {
            return ResultResponse.success();
        } else {
            return ResultResponse.error(ResultEnum.INTERNAL_SERVER_ERROR);
        }
    }

}

