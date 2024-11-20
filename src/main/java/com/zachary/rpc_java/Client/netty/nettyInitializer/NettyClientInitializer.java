package com.zachary.rpc_java.Client.netty.nettyInitializer;

import com.zachary.rpc_java.Client.netty.handler.NettyClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 配置netty对消息的处理机制
 * 1. 指定编码器
 * 2. 指定解码器
 * 3. 指定消息格式，消息长度，解决沾包问题
 * 4. 指定对接受的消息的处理handler
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // addLast没有先后顺序，netty通过加入的类实现的接口来自动识别类实现的是什么功能

        // 消息格式 【长度】【消息体】，解决沾包问题
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE, 0, 4, 0, 4
                )
        );
        // 计算当前待发送消息的长度，写入到前4个字节中
        pipeline.addLast(new LengthFieldPrepender(4));

        // 使用Java序列化方式作为编码器，netty的自带的解码编码支持传输这种结构
        pipeline.addLast(new ObjectEncoder());
        // 使用Netty中的ObjectDecoder，用于将字节流解码为Java对象
        // 在ObjectDecoder的构造函数中传入ClassResolver对象，用于解析类名并加载相应的类
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));

        pipeline.addLast(new NettyClientHandler());
    }
}
