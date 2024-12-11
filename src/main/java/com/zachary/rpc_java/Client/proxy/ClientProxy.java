package com.zachary.rpc_java.Client.proxy;

import com.zachary.rpc_java.Client.retry.GuavaRetry;
import com.zachary.rpc_java.Client.rpcClient.Impl.NettyRpcClient;
import com.zachary.rpc_java.Client.rpcClient.RpcClient;
import com.zachary.rpc_java.Client.serviceCenter.ServiceCenter;
import com.zachary.rpc_java.Client.serviceCenter.ZKServiceCenter;
import com.zachary.rpc_java.common.message.RpcRequest;
import com.zachary.rpc_java.common.message.RpcResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    // 传入参数service接口的class对象, 反射封装成一个request
    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;

    public ClientProxy() throws InterruptedException {
        rpcClient = new NettyRpcClient();
        serviceCenter = new ZKServiceCenter();
    }

    // jdk动态代理，每一次代理对象调用方法，都会经过此方法增强(反射获取request对象，socket发送到服务端)
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
        RpcResponse response;
        // 后续添加逻辑: 为保持幂等性，支队白名单上的服务进行重试
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            // 调用retry框架进行重试操作
            response = new GuavaRetry().sendServiceWithRetry(request, rpcClient);
        } else {
            // 只调用一次
            response = rpcClient.sendRequest(request);
        }
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
