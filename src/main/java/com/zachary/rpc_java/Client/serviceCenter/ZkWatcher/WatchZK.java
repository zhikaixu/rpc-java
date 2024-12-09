package com.zachary.rpc_java.Client.serviceCenter.ZkWatcher;

import com.zachary.rpc_java.Client.cache.ServiceCache;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

// 用于监听zookeeper
public class WatchZK {
    // curator提供的zookeeper客户端
    private CuratorFramework client;
    // 本地缓存
    private ServiceCache cache;

    public WatchZK(CuratorFramework client, ServiceCache cache) {
        this.client = client;
        this.cache = cache;
    }

    /**
     * 监听当前节点和子节点的 更新，创建，删除
     */
    public void watchToUpdate(String path) throws InterruptedException {
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                // type: 事件类型(枚举)
                // childData: 节点更新前的状态、数据
                // childData1: 节点更新后的状态、数据
                // 创建节点时: 节点刚被创建，不存在 更新前节点 ，所以childData为null
                // 删除节点时: 节点被删除，不存在 更新后节点 ，所以childData1为null
                // 节点创建时没有赋予值 create /curator/app1 只创建节点，在这种情况下，更新前节点的data为null
                switch (type.name()) {
                    case "NODE_CREATED": // 监听器第一次执行时节点存在也会触发事件
                        // 获取更新的节点的路径
                        String path = new String(childData1.getPath());
                        // 按照格式，读取
                        // path: /MyRPC/serviceName/address
                        String[] pathList = path.split("/");
                        if (pathList.length <= 2) {
                            break;
                        } else {
                            String serviceName = pathList[1];
                            String address = pathList[2];
                            // 将新注册的服务加入到本地缓存中
                            cache.addServiceToCache(serviceName, address);
                        }
                        break;
                    case "NODE_CHANGED":
                        if (childData.getData() != null) {
                            System.out.println("修改前的数据: " + new String(childData.getData()));
                        } else {
                            System.out.println("节点第一次赋值!");
                        }
                        System.out.println("修改后的数据: " + new String(childData1.getData()));
                        break;
                    case "NODE_DELETED":
                        String deletedPath = new String(childData.getPath());
                        String[] deletedPathList = deletedPath.split("/");
                        if (deletedPathList.length <= 2) {
                            break;
                        } else {
                            String serviceName = deletedPathList[1];
                            String address = deletedPathList[2];
                            cache.delete(serviceName, address);
                        }
                        System.out.println("删除节点: " + deletedPath);
                        break;
                    default:
                        break;
                }
            }
        });
        // 开启监听
        curatorCache.start();
    }
}
