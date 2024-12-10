package com.zachary.rpc_java.Client.serviceCenter.balance.impl;

import com.zachary.rpc_java.Client.serviceCenter.balance.LoadBalance;

import java.util.List;

public class RoundLoadBalance implements LoadBalance {
    private int choose = -1;
    @Override
    public String balance(List<String> addressList) {
        choose++;
        choose = choose % addressList.size();
        System.out.println("RoundLoadBalance选择了" + choose + "服务器");
        return addressList.get(choose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void deleteNode(String node) {

    }
}
