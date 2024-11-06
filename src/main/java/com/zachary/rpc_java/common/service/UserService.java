package com.zachary.rpc_java.common.service;


import com.zachary.rpc_java.common.pojo.User;

public interface UserService {
    // 客户端通过接口调用服务端的实现类
    User getUserByUserId(Integer id); // 获取用户
    Integer insertUserId(User user); // 插入用户
}
