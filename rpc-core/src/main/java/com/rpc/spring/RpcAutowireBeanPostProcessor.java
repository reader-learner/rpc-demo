package com.rpc.spring;

import com.rpc.annotation.RPCAutowire;
import com.rpc.annotation.RPCService;
import com.rpc.config.ServiceConfig;
import com.rpc.properties.RPCProperties;
import com.rpc.proxy.CustomProxy;
import com.rpc.transport.RPCClient;
import com.rpc.transport.netty.client.NettyRPCClient;
import extension.ExtensionLoader;
import factory.SingletonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

import static com.rpc.transport.AbstractRPCServer.serviceConfigs;

@Component
@Slf4j
public class RpcAutowireBeanPostProcessor implements BeanPostProcessor {
    private final RPCClient rpcClient;
    private final CustomProxy proxy;


    public RpcAutowireBeanPostProcessor() {
        rpcClient = SingletonFactory.getInstance(NettyRPCClient.class);
        proxy = ExtensionLoader.getExtensionLoader(CustomProxy.class)
                .getExtension(RPCProperties.getRPCProperties().getProxy());
    }

    /**
     * 扫描标注了 @RpcService 的服务
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        RPCService rpcService = bean.getClass().getAnnotation(RPCService.class);
        if (null != rpcService) {
            ServiceConfig serviceConfig = ServiceConfig.builder()
                    .service(bean)
                    .group(rpcService.group())
                    .serverName(RPCProperties.getRPCProperties().getApplicationName())
                    .version(rpcService.version()).build();
            serviceConfigs.add(serviceConfig);
            log.info(serviceConfig.getServerName() + "find rpc service: [{}]", serviceConfig.getServiceName());
        }
        return bean;
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
                        .serverName(rpcAutowire.serverName())
                        .build();
//                RpcDynamicProxy proxy = new RpcDynamicProxy(rpcClient, serviceConfig);
                proxy.setParams(rpcClient, serviceConfig);
                Object proxyObj = proxy.getProxy(interfaceClass);
                field.setAccessible(true);
                try {
                    field.set(bean, proxyObj);
                } catch (IllegalAccessException e) {
                    log.error("rpc autowire error");
                }
            }
        }
        return bean;
    }
}
