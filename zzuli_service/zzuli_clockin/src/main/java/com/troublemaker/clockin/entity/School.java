package com.troublemaker.clockin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import static com.baomidou.mybatisplus.annotation.IdType.INPUT;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.entity
 * @Author: troublemaker
 * @CreateTime: 2022-05-26  00:10
 */
@Data
public class School {
    @TableId(type = INPUT)
    private String uid;
    private String mobile;
    private String jtMobile;
    private String region;
    private String area;
    private String build;
    private String dorm;
    private String lat;
    private String lon;
    private String jzAddress;
    private String jzProvince;
    private String jzCity;
    private String jzDistrict;
}

