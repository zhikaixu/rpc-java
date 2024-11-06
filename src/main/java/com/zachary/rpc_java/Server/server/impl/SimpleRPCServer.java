package com.zachary.rpc_java.Server.server.impl;

import com.zachary.rpc_java.Server.provider.ServiceProvider;
import com.zachary.rpc_java.Server.server.RpcServer;
import com.zachary.rpc_java.Server.server.work.WorkThread;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@AllArgsConstructor
public class SimpleRPCServer implements RpcServer {
    private ServiceProvider serviceProvider;
    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            while (true) {
                // 没有connection会阻塞
                Socket socket = serverSocket.accept();
                // 创建一个新的线程执行处理
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
