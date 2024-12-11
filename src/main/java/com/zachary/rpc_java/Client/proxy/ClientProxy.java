package com.zachary.rpc_java.Client.proxy;

import com.zachary.rpc_java.Client.circuitBreaker.CircuitBreaker;
import com.zachary.rpc_java.Client.circuitBreaker.CircuitBreakerProvider;
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
    private CircuitBreakerProvider circuitBreakerProvider;
    public ClientProxy() throws InterruptedException {
        rpcClient = new NettyRpcClient();
        serviceCenter = new ZKServiceCenter();
        circuitBreakerProvider = new CircuitBreakerProvider();
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

        // 获取熔断器
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        // 判断熔断器是否允许请求经过
        if (!circuitBreaker.allowRequest()) {
            // 可以针对熔断做特殊处理，返回特殊值
            return null;
        }

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
        // 记录response的状态，上报给熔断器
        if (response.getCode() == 200) {
            circuitBreaker.recordSuccess();
        }
        if (response.getCode() == 500) {
            circuitBreaker.recordFailure();
        }
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
