package com.zachary.rpc_java.Client.rpcClient.Impl;

import com.zachary.rpc_java.Client.rpcClient.RpcClient;
import com.zachary.rpc_java.common.message.RpcRequest;
import com.zachary.rpc_java.common.message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 是对原来的IOClient的重写
 */
public class SimpleSocketRpcClient implements RpcClient {
    private String host;
    private int port;
    public SimpleSocketRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(request);
            oos.flush();

            RpcResponse response = (RpcResponse) ois.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
