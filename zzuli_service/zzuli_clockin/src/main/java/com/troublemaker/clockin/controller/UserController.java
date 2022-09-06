package com.troublemaker.clockin.controller;

import com.troublemaker.clockin.annotation.JWTToken;
import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.controller
 * @Author: troublemaker
 * @CreateTime: 2022-08-30  16:00
 * @Description: TODO
 * @Version: 1.0
 */
@CrossOrigin
@RestController
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/changeClockType/{clockType}")
    @JWTToken
    public ResultResponse changeClockType(@RequestHeader(value = "Authorization") String token, @PathVariable(name = "clockType") Integer clockType) {
        return service.changeClockType(token, clockType);
    }

}

