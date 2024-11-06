package com.zachary.rpc_java.Client;

import com.zachary.rpc_java.Client.proxy.ClientProxy;
import com.zachary.rpc_java.common.pojo.User;
import com.zachary.rpc_java.common.service.UserService;

public class RpcClientMain {
    public static void main(String[] args) {
        // 创建代理对象
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
        // 通过ClientProxy对象获取代理对象
        UserService proxy = clientProxy.getProxy(UserService.class);
        // 调用代理对象方法
        User user = proxy.getUserByUserId(1);
        System.out.println("从服务端得到的user=" + user.toString());
    }
}
