package com.zachary.rpc_java.common.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RpcResponse implements Serializable {
    // 状态码
    private int code;
    // 状态信息
    private String message;
    // 具体数据
    private Object data;
    // 构造成功信息
    public static RpcResponse success(Object data) {
        return RpcResponse.builder().code(200).data(data).build();
    }
    // 构造失败信息
    public static RpcResponse fail() {
        return RpcResponse.builder().code(500).data("服务器发生错误").build();
    }

    public static void main(String[] args) {
        RpcResponse response = RpcResponse.fail();
        System.out.println(response.data);
    }
}
