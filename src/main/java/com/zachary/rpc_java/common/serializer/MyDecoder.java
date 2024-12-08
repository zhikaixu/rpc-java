package com.zachary.rpc_java.common.serializer;

import com.zachary.rpc_java.common.message.MessageType;
import com.zachary.rpc_java.common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. 读取消息类型
        short messageType = in.readShort();
        // 暂时只支持request与response
        if (messageType != MessageType.REQUEST.getCode() && messageType != MessageType.RESPONSE.getCode()) {
            System.out.println("暂不支持此种数据");
            return;
        }
        // 2. 读取序列化的方式和类型
        short serializeType = in.readShort();
        Serializer serializer = Serializer.getSerializerByCode(serializeType);
        if (serializer == null) {
            throw new RuntimeException("不存在对应的序列化器");
        }
        // 3. 读取序列化数组长度
        int length = in.readInt();
        // 4. 读取序列化数组
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object deserialize = serializer.deserialize(bytes, messageType);
        out.add(deserialize);
    }
}
