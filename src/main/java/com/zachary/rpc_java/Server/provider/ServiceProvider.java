package com.zachary.rpc_java.Server.provider;

import com.zachary.rpc_java.Server.serviceRegister.ServiceRegister;
import com.zachary.rpc_java.Server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @apiNote 本地服务存放器(注册服务到本地集合中)
 */
public class ServiceProvider {
    // 集中存放服务实例
    private Map<String, Object> interfaceProvider;

    private int port;
    private String host;

    // 注册服务类：用于注册服务到注册中心
    // 在服务端上线时调用ServiceProvider的添加服务逻辑，使得本地注册服务时也注册服务到注册中心上
    private ServiceRegister serviceRegister;

    public ServiceProvider(String host, int port) {
        this.interfaceProvider = new HashMap<>();
        // 需要传入服务端自身的网络地址
        this.host = host;
        this.port = port;
        this.serviceRegister = new ZKServiceRegister();
    }

    // 注册本地服务
    public void provideServiceInterface(Object service) {
        // 通过反射得到service name
        String serviceName = service.getClass().getName();
        // 通过反射得到interfaces
        // 一个service可能有多个interface
        Class<?>[] interfaces = service.getClass().getInterfaces();

        for (Class<?> interfaceClass : interfaces) {
            String interfaceName = interfaceClass.getName();
            // 本地注册
            interfaceProvider.put(interfaceName, service);
            // 注册中心注册
            serviceRegister.register(interfaceName, new InetSocketAddress(host, port));
        }
    }

    // 获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}
