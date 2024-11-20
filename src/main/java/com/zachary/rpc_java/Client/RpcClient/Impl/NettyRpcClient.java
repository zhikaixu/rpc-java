package com.zachary.rpc_java.Client.RpcClient.Impl;

import com.zachary.rpc_java.Client.netty.nettyInitializer.NettyClientInitializer;
import com.zachary.rpc_java.Client.RpcClient.RpcClient;
import com.zachary.rpc_java.common.message.RpcRequest;
import com.zachary.rpc_java.common.message.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

public class NettyRpcClient implements RpcClient {
    private String host;
    private int port;
    public static final Bootstrap bootstrap;
    public static final EventLoopGroup eventLoopGroup;
    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Netty客户端初始化
    static {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            // 创建channelFuture对象，代表着一个操作事件，sync方法表示
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();

            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();

            System.out.println(response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
