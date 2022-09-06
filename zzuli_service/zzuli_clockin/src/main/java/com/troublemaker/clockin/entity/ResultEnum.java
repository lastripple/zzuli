package com.troublemaker.clockin.entity;

import com.troublemaker.clockin.service.BaseResultInfoInterface;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.entity
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  23:05
 * @Description: 异常处理枚举类
 * @Version: 1.0
 */
public enum ResultEnum implements BaseResultInfoInterface {
    // 数据操作错误定义
    SUCCESS("2000", "成功!"),
    BODY_NOT_MATCH("4000", "请求的账号或密码错误!"),
    SIGNATURE_NOT_MATCH("4001", "请求的数字签名不匹配!"),
    NOT_FOUND("4004", "未找到该资源!"),
    INTERNAL_SERVER_ERROR("5000", "服务器内部错误!"),
    SERVER_BUSY("5003", "服务器正忙，请稍后再试!");


    private final String resultCode;

    private final String resultMsg;

    ResultEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
