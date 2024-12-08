package com.zachary.rpc_java.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RpcResponse implements Serializable {
    // 状态码
    private int code;
    // 状态信息
    private String message;
    // 更新: 加入传输数据的类型，以便在自定义序列化器中解析
    private Class<?> dataType;
    // 具体数据
    private Object data;
    // 构造成功信息
    public static RpcResponse success(Object data) {
        return RpcResponse.builder().code(200).dataType(data.getClass()).data(data).build();
    }
    // 构造失败信息
    public static RpcResponse fail() {
        return RpcResponse.builder().code(500).data("服务器发生错误").build();
    }
}
