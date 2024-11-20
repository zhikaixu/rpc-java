package com.zachary.rpc_java.Client.RpcClient;

import com.zachary.rpc_java.common.message.RpcRequest;
import com.zachary.rpc_java.common.message.RpcResponse;

public interface RpcClient {

    // 定义底层通信的方法
    RpcResponse sendRequest(RpcRequest request);
}
