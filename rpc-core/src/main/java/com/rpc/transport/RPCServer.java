package com.rpc.transport;

import com.rpc.config.ServiceConfig;

public interface RPCServer {

    /**
     * 启动服务器
     */
    void start();

    void publishService(ServiceConfig config);
}
