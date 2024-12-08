package com.zachary.rpc_java.common.serializer.mySerializer;

public interface Serializer {
    // 把对象序列化成字节数组
    byte[] serialize(Object obj);

    // 字节数组反序列化成对象
    // java自带序列化方式不使用messageType能得到响应的对象（字节数组包含类信息）
    // 其他方式需指定消息格式，再根据message转化成响应对象
    Object deserialize(byte[] bytes, int messageType);

    // 返回使用的序列器：0: java自带序列化方式，1: json序列化方式
    int getType();

    // 根据序号取出序列化器
    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }


}
