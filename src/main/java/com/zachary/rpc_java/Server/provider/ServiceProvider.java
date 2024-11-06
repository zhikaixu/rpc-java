package com.zachary.rpc_java.Server.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @apiNote 本地服务存放器
 */
public class ServiceProvider {
    // 集中存放服务实例
    private Map<String, Object> interfaceProvider;

    public ServiceProvider() {
        this.interfaceProvider = new HashMap<>();
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
            interfaceProvider.put(interfaceName, service);
        }
    }

    // 获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}
