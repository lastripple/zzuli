package com.troublemaker.clockin.handler;

import com.troublemaker.clockin.entity.ResultEnum;
import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.handler
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  23:21
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @description: 处理自定义的业务异常
     * @author: troublemaker
     * @date:  23:22
     * @param: [req, e]
     * @return: com.troublemaker.clockin.entity.ResultResponse
     **/
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public ResultResponse bizExceptionHandler(HttpServletRequest req, BizException e){
        log.error("发生业务异常！原因是：{}",e.getErrorMsg());
        return ResultResponse.error(e.getErrorCode(),e.getErrorMsg());
    }

    /**
     * @description: 处理空指针的异常
     * @author: troublemaker
     * @date:  23:23
     * @param: [req, e]
     * @return: com.troublemaker.clockin.entity.ResultResponse
     **/
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest req, NullPointerException e){
        log.error("发生空指针异常！原因是:",e);
        return ResultResponse.error(ResultEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * @description: 处理其他异常
     * @author: troublemaker
     * @date:  23:23
     * @param: [req, e]
     * @return: com.troublemaker.clockin.entity.ResultResponse
     **/
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest req, Exception e){
        log.error("未知异常！原因是:",e);
        return ResultResponse.error(ResultEnum.INTERNAL_SERVER_ERROR);
    }
}

