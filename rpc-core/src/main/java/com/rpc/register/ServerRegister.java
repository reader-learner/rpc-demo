package com.rpc.register;

import com.rpc.transport.dto.ServiceInstance;
import extension.SPI;

import java.net.InetSocketAddress;
import java.util.List;

@SPI
public interface ServerRegister {
    /**
     * 注册服务
     *
     * @param serverName     服务名称
     * @param serviceAddress 服务地址
     */
    void registerService(String serverName, InetSocketAddress serviceAddress);

    void clearServer(InetSocketAddress serviceAddress);


}
