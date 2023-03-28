package com.rpc.provider;

import extension.SPI;

import java.net.InetSocketAddress;

/**
 * 服务端 服务实例提供
 */
@SPI
public interface ServiceProvider {


    /**
     * 发布服务 key : value => serviceName : serviceBean
     * @param serviceName 服务名称
     * @param serviceBean 服务实例
     * @param serviceAddress 服务地址
     * @param <T> 实例类型
     */
    <T> void addService(String serviceName, T serviceBean, InetSocketAddress serviceAddress);

    /**
     * 根据服务名称获取服务实例
     * @param serviceName 服务名称
     * @return 服务实例
     */
    Object getService(String serviceName);

    /**
     * 清理注册服务
     * @param serviceAddress 本机服务器地址
     */
    void clearService(InetSocketAddress serviceAddress);
}
