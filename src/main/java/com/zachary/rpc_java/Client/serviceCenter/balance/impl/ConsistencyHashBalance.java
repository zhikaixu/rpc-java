package com.zachary.rpc_java.Client.serviceCenter.balance.impl;

import com.zachary.rpc_java.Client.serviceCenter.balance.LoadBalance;

import java.util.*;

public class ConsistencyHashBalance implements LoadBalance {
    // 虚拟节点的个数
    private static final int VIRTUAL_NUM = 5;

    // 虚拟节点分配，key是hash值，value是虚拟节点服务器名称
    private static SortedMap<Integer, String> shards = new TreeMap<>();

    // 真实节点列表
    private static List<String> realNodes = new LinkedList<>();

    // 模拟初始服务器
    private static String[] servers = null;

    /**
     *
     * @param serverList 真实节点
     */
    private static void init(List<String> serverList) {
        for (String server : serverList) {
            realNodes.add(server);
            System.out.println("真实节点[" + server + "] 被添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + ", 被添加");
            }
        }
    }

    /**
     * 获取被分配的节点名
     * @param node
     * @param serverList
     * @return
     */
    public static String getServer(String node, List<String> serverList) {
        init(serverList);
        int hash = getHash(node);
        Integer key = null;
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            key = shards.lastKey();
        } else {
            key = subMap.firstKey();
        }
        String virtualNode = shards.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    @Override
    public String balance(List<String> addressList) {
        String random = UUID.randomUUID().toString();
        return getServer(random, addressList);
    }

    @Override
    public void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            System.out.println("真实节点[" + node + "] 上线添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + ", 被添加");
            }
        }
    }

    @Override
    public void deleteNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            System.out.println("真实节点[" + node + "] 下线移除");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + ", 被移除");
            }
        }
    }

    /**
     * FNV1_32_HASH算法
     * @param str
     * @return
     */
    public static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

//    public static void main(String[] args) {
//        for (int i = 0; i < 10; i++) {
//            String random = UUID.randomUUID().toString();
//            System.out.println(getHash(random));
//        }
//    }
}
