package com.rpc.spring;

import com.rpc.annotation.RPCAutowire;
import com.rpc.config.ServiceConfig;
import com.rpc.properties.RPCProperties;
import com.rpc.proxy.CustomProxy;
import com.rpc.proxy.cglib.CglibDynamicProxy;
import com.rpc.transport.RPCClient;
import com.rpc.transport.netty.client.NettyRPCClient;
import extension.ExtensionLoader;
import factory.SingletonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
@Slf4j
public class RpcAutowireBeanPostProcessor implements BeanPostProcessor {
    private final RPCClient rpcClient;
    private final CustomProxy proxy;

    public RpcAutowireBeanPostProcessor() {
        rpcClient = SingletonFactory.getInstance(NettyRPCClient.class);
        proxy = ExtensionLoader.getExtensionLoader(CustomProxy.class)
                .getExtension(RPCProperties.proxy);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            RPCAutowire rpcAutowire = field.getAnnotation(RPCAutowire.class);
            if (rpcAutowire != null) {
                Class<?> interfaceClass = field.getType();
                ServiceConfig serviceConfig = ServiceConfig.builder()
                        .service(bean)
                        .group(rpcAutowire.group())
                        .version(rpcAutowire.version())
                        .build();
//                RpcDynamicProxy proxy = new RpcDynamicProxy(rpcClient, serviceConfig);
                proxy.setParams(rpcClient, serviceConfig);
                Object proxyObj = proxy.getProxy(interfaceClass);
                field.setAccessible(true);
                try {
                    field.set(bean, proxyObj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
