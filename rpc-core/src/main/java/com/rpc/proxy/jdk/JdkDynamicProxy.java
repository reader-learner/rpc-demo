package com.rpc.proxy.jdk;

import com.rpc.config.ServiceConfig;
import com.rpc.proxy.AbstractCustomProxy;
import com.rpc.transport.RPCClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkDynamicProxy extends AbstractCustomProxy implements InvocationHandler {
    public JdkDynamicProxy(RPCClient rpcClient, ServiceConfig config) {
        super(rpcClient, config);
    }

    public JdkDynamicProxy() {
    }

    @Override
    public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JdkDynamicProxy jdkDynamicProxy = new JdkDynamicProxy(rpcClient, config);
        Object proxy = jdkDynamicProxy.getProxy(serviceClass);
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        return method.invoke(proxy, parameters);
    }

    public <T> T getProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return sendRequestAndGetResponse(method, args);
    }
}
