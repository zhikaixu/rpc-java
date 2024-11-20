package com.zachary.rpc_java.Server.server.impl;

import com.zachary.rpc_java.Server.provider.ServiceProvider;
import com.zachary.rpc_java.Server.server.RpcServer;
import com.zachary.rpc_java.Server.netty.nettyInitializer.NettyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NettyRPCServer implements RpcServer {

    private ServiceProvider serviceProvider;
    @Override
    public void start(int port) {
        // netty服务线程组boss负责建立连接，work负责具体的请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        System.out.println("netty服务端启动了");
        try {
            // 启动netty服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 初始化
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    // TODO: 创建NettyServerInitializer，配置netty对消息的处理机制
                    .childHandler(new NettyServerInitializer(serviceProvider));
            // 同步阻塞
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 死循环监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
