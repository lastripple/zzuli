package com.troublemaker.clockin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.troublemaker.clockin.config.JWTTokenConfiguration;
import com.troublemaker.clockin.entity.ResultEnum;
import com.troublemaker.clockin.entity.ResultResponse;
import com.troublemaker.clockin.entity.User;
import com.troublemaker.clockin.mapper.UserMapper;
import com.troublemaker.clockin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.service
 * @Author: troublemaker
 * @CreateTime: 2022-08-28  00:29
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JWTTokenConfiguration jwtToken;

    @Autowired
    public UserServiceImpl(JWTTokenConfiguration jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public boolean existUserByUsername(User user) {
        User existUser = baseMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        return existUser != null;
    }

    @Override
    public boolean updatePasswordByUsername(User user) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("password", user.getPassword()).eq("username", user.getUsername());
        int i = baseMapper.update(null, updateWrapper);
        return i > 0;
    }

    @Override
    public ResultResponse changeClockType(String token, Integer num) {
        String userID = jwtToken.verifyToken(token).get("userID").toString().replace("\"", "");
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("clock_type", num).eq("uid", userID);
        int i = baseMapper.update(null, updateWrapper);
        if (i > 0) {
            return ResultResponse.success();
        } else
            return ResultResponse.error(ResultEnum.INTERNAL_SERVER_ERROR);
    }

    @Override
    public boolean changeClockStatus(User user) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("clock_status", 1).eq("username", user.getUsername());
        int i = baseMapper.update(null, updateWrapper);
        return i > 0;
    }

    @Override
    public boolean reSetClockStatus() {
        List<User> users = getUsers();
        int sum = 0;
        for (User user : users) {
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("clock_status", 0).eq("username", user.getUsername());
            sum += baseMapper.update(null, updateWrapper);
        }
        return sum == users.size();
    }

    @Override
    public User getUserByUsername(User user) {
        return baseMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
    }

    @Override
    public List<User> getUsers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ne("clock_type", "2");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<User> getUnClockUsers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ne("clock_type", "2").eq("clock_status", "0");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean addUser(User user) {
        int i = baseMapper.insert(user);
        return i > 0;
    }

}

