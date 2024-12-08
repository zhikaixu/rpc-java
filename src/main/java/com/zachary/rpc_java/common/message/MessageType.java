package com.zachary.rpc_java.common.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageType {
    REQUEST(0), RESPONSE(1);
    private int code;
    public int getCode() {
        return code;
    }
}
