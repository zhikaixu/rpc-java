package com.zachary.rpc_java.Server;

import com.zachary.rpc_java.Server.provider.ServiceProvider;
import com.zachary.rpc_java.Server.server.RpcServer;
import com.zachary.rpc_java.Server.server.impl.NettyRPCServer;
import com.zachary.rpc_java.Server.server.impl.SimpleRPCServer;
import com.zachary.rpc_java.Server.server.impl.ThreadPoolRPCServer;
import com.zachary.rpc_java.common.service.UserService;
import com.zachary.rpc_java.common.service.impl.UserServiceImpl;

public class RpcServerMain {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider();

        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRPCServer(serviceProvider);

        rpcServer.start(9999);
    }
}
