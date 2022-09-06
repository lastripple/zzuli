package com.troublemaker.clockin.controller;

import com.troublemaker.clockin.annotation.JWTToken;
import com.troublemaker.clockin.entity.Home;
import com.troublemaker.clockin.entity.HomeInputData;
import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;

import static com.troublemaker.clockin.entity.ResultResponse.success;


/**
 * @author Troublemaker
 * @date 2022- 04 28 21:25
 */
@CrossOrigin
@RestController
public class HomeController {

    private final HomeService service;

    private final  ServletContext servletContext;

    @Autowired
    public HomeController(HomeService service, ServletContext servletContext) {
        this.service = service;
        this.servletContext = servletContext;
    }

    @PostMapping("/addHomeClockInfo")
    @JWTToken
    public ResultResponse addHomeClockInfo(@RequestHeader(value = "Authorization") String token, @RequestBody Home home) {
        return service.addHomeClockInfo(token, home);
    }

    @GetMapping("/getHomeClockInfo")
    @JWTToken
    public ResultResponse getHomeClockInfo(@RequestHeader(value = "Authorization") String token) {
        HomeInputData homeClockInfo = service.getHomeClockInfo(token, servletContext);
        return success(homeClockInfo);
    }
}

