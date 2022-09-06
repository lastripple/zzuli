package com.troublemaker.clockin.entity;

import com.alibaba.fastjson.JSONObject;
import com.troublemaker.clockin.service.BaseResultInfoInterface;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.entity
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  23:13
 * @Description: TODO
 * @Version: 1.0
 */
public class ResultResponse {

    private String code;


    private String message;


    private Object result;

    public ResultResponse() {
    }

    public ResultResponse(BaseResultInfoInterface errorInfo) {
        this.code = errorInfo.getResultCode();
        this.message = errorInfo.getResultMsg();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * @description: 成功
     * @author: troublemaker
     * @date:  23:18
     * @param: []
     * @return: com.troublemaker.clockin.entity.ResultResponse
     **/
    public static ResultResponse success() {
        return success(null);
    }

   /**
    * @description: 成功
    * @author: troublemaker
    * @date:  23:19
    * @param: [data]
    * @return: com.troublemaker.clockin.entity.ResultResponse
    **/
    public static ResultResponse success(Object data) {
        ResultResponse rb = new ResultResponse();
        rb.setCode(ResultEnum.SUCCESS.getResultCode());
        rb.setMessage(ResultEnum.SUCCESS.getResultMsg());
        rb.setResult(data);
        return rb;
    }

    /**
     * @description: 失败
     * @author: troublemaker
     * @date:  23:19
     * @param: [errorInfo]
     * @return: com.troublemaker.clockin.entity.ResultResponse
     **/
    public static ResultResponse error(BaseResultInfoInterface errorInfo) {
        ResultResponse rb = new ResultResponse();
        rb.setCode(errorInfo.getResultCode());
        rb.setMessage(errorInfo.getResultMsg());
        rb.setResult(null);
        return rb;
    }

    /**
     * @description: 失败
     * @author: troublemaker
     * @date:  23:20
     * @param: [code, message]
     * @return: com.troublemaker.clockin.entity.ResultResponse
     **/
    public static ResultResponse error(String code, String message) {
        ResultResponse rb = new ResultResponse();
        rb.setCode(code);
        rb.setMessage(message);
        rb.setResult(null);
        return rb;
    }

    /**
     * @description: 失败
     * @author: troublemaker
     * @date:  23:20
     * @param: [message]
     * @return: com.troublemaker.clockin.entity.ResultResponse
     **/
    public static ResultResponse error(String message) {
        ResultResponse rb = new ResultResponse();
        rb.setCode("-1");
        rb.setMessage(message);
        rb.setResult(null);
        return rb;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

