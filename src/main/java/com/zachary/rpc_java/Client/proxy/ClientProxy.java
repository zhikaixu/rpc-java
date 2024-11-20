package com.zachary.rpc_java.Client.proxy;

import com.zachary.rpc_java.Client.RpcClient.Impl.NettyRpcClient;
import com.zachary.rpc_java.Client.RpcClient.Impl.SimpleSocketRpcClient;
import com.zachary.rpc_java.Client.RpcClient.RpcClient;
import com.zachary.rpc_java.common.message.RpcRequest;
import com.zachary.rpc_java.common.message.RpcResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {

    private RpcClient rpcClient;

    public ClientProxy(String host, int port, int choose) {
        switch (choose) {
            case 0:
                rpcClient = new SimpleSocketRpcClient(host, port);
                break;
            case 1:
                rpcClient = new NettyRpcClient(host, port);
        }
    }

    public ClientProxy(String host, int port) {
        rpcClient = new NettyRpcClient(host, port);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建request
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .build();

        // 和服务端进行数据传输
        RpcResponse response = rpcClient.sendRequest(request);
        assert response != null;
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
