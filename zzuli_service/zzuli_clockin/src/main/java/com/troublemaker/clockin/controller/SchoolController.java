package com.troublemaker.clockin.controller;

import com.troublemaker.clockin.annotation.JWTToken;
import com.troublemaker.clockin.entity.*;
import com.troublemaker.clockin.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;

import static com.troublemaker.clockin.entity.ResultResponse.success;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.controller
 * @Author: troublemaker
 * @CreateTime: 2022-08-28  01:26
 * @Description: TODO
 * @Version: 1.0
 */
@CrossOrigin
@RestController
public class SchoolController {
    private final SchoolService service;

    private final ServletContext servletContext;

    @Autowired
    public SchoolController(SchoolService service, ServletContext servletContext) {
        this.service = service;
        this.servletContext = servletContext;
    }

    @PostMapping("/addSchoolClockInfo")
    @JWTToken
    public ResultResponse addSchoolClockInfo(@RequestHeader(value = "Authorization") String token, @RequestBody School school) {
        return service.addSchoolClockInfo(token, school);
    }

    @GetMapping("/getSchoolClockInfo")
    @JWTToken
    public ResultResponse getSchoolClockInfo(@RequestHeader(value = "Authorization") String token) {
        SchoolInputData schoolInputData = service.getSchoolClockInfo(token, servletContext);
        return success(schoolInputData);
    }
}

