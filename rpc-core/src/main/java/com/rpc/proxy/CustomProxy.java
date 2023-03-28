package com.rpc.proxy;

import com.rpc.config.ServiceConfig;
import com.rpc.transport.RPCClient;
import extension.SPI;
import org.apache.zookeeper.server.ServerConfig;

import java.lang.reflect.InvocationTargetException;

@SPI
public interface CustomProxy {
    void setParams(RPCClient rpcClient, ServiceConfig config);

    <T> T getProxy(Class<T> interfaceClass);

    Object invokeMethod(Object serviceBean, Class<?> serviceClass,
                        String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
