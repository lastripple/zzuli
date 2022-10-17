package com.troublemaker.clockin.controller;

import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.entity.User;
import com.troublemaker.clockin.service.LoginService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;



/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.controller
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  23:40
 * @Version: 1.0
 */
@CrossOrigin
@RestController
public class LoginController {
    private final LoginService loginService;
    private final HttpClientBuilder httpClientBuilder;
    private final ServletContext servletContext;

    @Autowired
    public LoginController(LoginService loginService, HttpClientBuilder httpClientBuilder, ServletContext servletContext) {
        this.loginService = loginService;
        this.httpClientBuilder = httpClientBuilder;
        this.servletContext = servletContext;
    }

    @PostMapping("/login")
    public ResultResponse login(@RequestBody User user) {
        CloseableHttpClient httpClient = httpClientBuilder.build();
        System.out.println("有人进入系统,创建httpClient" + httpClient);
        servletContext.setAttribute(user.getUsername(), httpClient);
        return loginService.verifyUserAndISToken(httpClient, user);
    }
}

