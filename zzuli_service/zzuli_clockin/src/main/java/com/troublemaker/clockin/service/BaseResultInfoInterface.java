package com.troublemaker.clockin.service;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  23:03
 * @Description:  服务接口类
 * @Version: 1.0
 */
public interface BaseResultInfoInterface {
    /**
     * @description: 错误码
     * @author: troublemaker
     * @date:  23:04
     * @param: []
     * @return: java.lang.String
     **/
    String getResultCode();

    /**
     * @description: 错误描述
     * @author: troublemaker
     * @date:  23:04
     * @param: []
     * @return: java.lang.String
     **/
    String getResultMsg();
}
