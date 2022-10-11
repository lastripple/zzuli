package com.troublemaker.clockin.service.impl;

import com.troublemaker.clockin.config.JWTTokenConfiguration;
import com.troublemaker.clockin.entity.ResultEnum;
import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.entity.User;
import com.troublemaker.clockin.service.LoginService;
import com.troublemaker.clockin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.troublemaker.utils.encryptionutils.EncryptionUtil.getBase64Password;
import static com.troublemaker.utils.httputils.HttpClientUtils.*;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  23:50
 * @Version: 1.0
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final UserService userService;
    private final JWTTokenConfiguration jwtToken;

    private final ServletContext servletContext;

    @Autowired
    public LoginServiceImpl(UserService userService, JWTTokenConfiguration jwtToken, ServletContext servletContext) {
        this.userService = userService;
        this.jwtToken = jwtToken;
        this.servletContext = servletContext;
    }

    @Override
    public Map<String, String> loginMap(User user, String lt) {
        //Base64加密
        String password = getBase64Password(user.getPassword());
        //转为map
        HashMap<String, String> loginMap = new HashMap<>(1);
        loginMap.put("username", user.getUsername());
        loginMap.put("password", password);
        loginMap.put("secret", "");
        loginMap.put("accountLogin", "");
        loginMap.put("lt", lt);
        loginMap.put("execution", "e1s1");
        loginMap.put("_eventId", "submit");
        return loginMap;
    }

    @Override
    public String getLt(CloseableHttpClient client, String url) {
        String entityStr = doGetForEntity(client, url);
        return Jsoup.parse(entityStr).select("[name='lt']").attr("value");
    }

    @Override
    public String login(CloseableHttpClient client, String url, Map<String, String> map) {
        String entityStr = doApplicationPost(client, url, map);
        return Jsoup.parse(entityStr).select("title").html();
    }

    @Override
    public boolean verifyLogin(CloseableHttpClient client, User user) {
        String url = "http://kys.zzuli.edu.cn/cas/login";
        String lt = getLt(client, url);
        String result = login(client, url, loginMap(user, lt));
        return "统一身份认证平台-登录成功".equals(result);
    }


    public ResultResponse verifyUserAndISToken(CloseableHttpClient client, User user) {
        // 服务器校验账号密码
        if (verifyLogin(client, user)) {
            // 查看数据库数据
            if (userService.existUserByUsername(user)) {
                // 检验密码
                User existUser = userService.getUserByUsername(user);
                if (existUser.getPassword().equals(user.getPassword())) {
                    // 签发token
                    Map<String, String> token = jwtToken.createToken(existUser.getUid());
                    return ResultResponse.success(token);
                } else {
                    // 更新密码
                    if (userService.updatePasswordByUsername(user)) {
                        // 签发token
                        Map<String, String> token = jwtToken.createToken(existUser.getUid());
                        return ResultResponse.success(token);
                    } else {
                        return new ResultResponse(ResultEnum.INTERNAL_SERVER_ERROR);
                    }
                }
            } else {
                // 添加数据
                if (userService.addUser(user)) {
                    // 签发token
                    Map<String, String> token = jwtToken.createToken(user.getUid());
                    return ResultResponse.success(token);
                } else {
                    return new ResultResponse(ResultEnum.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            System.out.println("验证失败，关闭 client 并 removeAttribute：" + client);
            servletContext.removeAttribute(user.getUsername());
            clientClose(client);
            return new ResultResponse(ResultEnum.BODY_NOT_MATCH);
        }

    }

    @Override
    public void cleanClient() {
        log.info("-----------------清理启动-------------------");
        int count = 0;
        Enumeration<String> e = servletContext.getAttributeNames();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            Object value = servletContext.getAttribute(key);
            servletContext.removeAttribute(key);
            if (value instanceof CloseableHttpClient) {
                count++;
                CloseableHttpClient client = (CloseableHttpClient) value;
                clientClose(client);
            }
        }
        log.info("总数: " + count);
        log.info("-----------------清理完成-------------------");
    }
}

