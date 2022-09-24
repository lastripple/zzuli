package com.troublemaker.order.entity;

import lombok.Data;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.order.entity
 * @Author: troublemaker
 * @CreateTime: 2022-09-24  08:11
 * @Description: TODO
 * @Version: 1.0
 */

@Data
public class YamlOrderData {
    private int intervalTime;
    private int retryCount;
}

