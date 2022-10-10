package com.troublemaker.clockin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.entity.User;

import java.util.List;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service
 * @Author: troublemaker
 * @CreateTime: 2022-08-28  00:21
 * @Description: TODO
 * @Version: 1.0
 */
public interface UserService extends IService<User> {

    boolean existUserByUsername(User user);

    User getUserByUsername(User user);

    List<User> getUsers();
    List<User> getUnClockUsers();

    boolean addUser(User user);

    boolean updatePasswordByUsername(User user);

    ResultResponse changeClockType(String token, Integer clockType);

    boolean changeClockStatus(User user);

    boolean reSetClockStatus();
}

