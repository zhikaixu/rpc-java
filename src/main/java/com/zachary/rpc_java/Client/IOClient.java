package com.zachary.rpc_java.Client;

import com.zachary.rpc_java.common.message.RpcRequest;
import com.zachary.rpc_java.common.message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class IOClient {
    // 负责底层与服务端的通信，发送request，返回response
    public static RpcResponse sendRequest(String host, int port, RpcRequest request) {
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
