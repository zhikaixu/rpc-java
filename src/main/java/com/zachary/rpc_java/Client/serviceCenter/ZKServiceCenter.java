package com.zachary.rpc_java.Client.serviceCenter;

import com.zachary.rpc_java.Client.cache.ServiceCache;
import com.zachary.rpc_java.Client.serviceCenter.ZkWatcher.WatchZK;
import com.zachary.rpc_java.Client.serviceCenter.balance.impl.ConsistencyHashBalance;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceCenter implements ServiceCenter{
    // curator提供的zookeeper客户端
    private CuratorFramework client;
    // zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";
    // serviceCache
    private ServiceCache cache;

    // zookeeper客户端初始化，并与zookeeper服务端进行连接
    public ZKServiceCenter() throws InterruptedException {
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime有关系
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime的2倍和20倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper连接成功");
        // 初始化本地缓存
        cache = new ServiceCache();
        // 加入zookeeper事件监听器
        WatchZK watcher = new WatchZK(client, cache);
        watcher.watchToUpdate(ROOT_PATH);
    }

    // 根据服务名(接口名)返回地址
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            // 先从本地缓存中找
            List<String> serviceList = cache.getServiceFromCache(serviceName);
            // 如果找不到，再去zookeeper中找
            // 这种情况基本不会发生，只会出现在初始化阶段
            if (serviceList == null) {
                serviceList = client.getChildren().forPath("/" + serviceName);
            }
            // 负载均衡得到地址
            String address = new ConsistencyHashBalance().balance(serviceList);
            return parseAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 地址 -> XX.XX.XX.XX:port字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getAddress() + ":" + serverAddress.getPort();
    }

    // XX.XX.XX.XX:port字符串 -> 地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
