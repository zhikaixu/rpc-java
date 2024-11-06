package com.zachary.rpc_java.common.service.impl;

import com.zachary.rpc_java.common.pojo.User;
import com.zachary.rpc_java.common.service.UserService;

import java.util.Random;

public class UserServiceImpl implements UserService {

    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("客户端查询了" + id + "的用户");
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        User user = User.builder()
                .id(id)
                .sex(random.nextBoolean()).build();
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入数据成功" + user.getUsername());
        return user.getId();
    }
}
