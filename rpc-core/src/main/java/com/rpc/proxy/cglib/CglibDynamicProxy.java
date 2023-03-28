package com.rpc.proxy.cglib;

import com.rpc.config.ServiceConfig;
import com.rpc.proxy.AbstractCustomProxy;
import com.rpc.transport.RPCClient;
import lombok.Data;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
public class CglibDynamicProxy extends AbstractCustomProxy {
    // 通过CGLIB动态代理获取代理对象的过程
    // 创建Enhancer对象，类似于JDK动态代理的Proxy类
    private static Enhancer enhancer = new Enhancer();

    public CglibDynamicProxy() {
    }

    public CglibDynamicProxy(RPCClient rpcClient, ServiceConfig config) {
        super(rpcClient, config);
    }

    @Override
    public <T> T getProxy(Class<T> interfaceClass) {
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new CglibClientInterceptor());
        return (T) enhancer.create();
    }

    @Override
    public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return getProxy(serviceClass).getClass().getMethod(methodName, parameterTypes).invoke(serviceBean, parameters);
    }

    class CglibClientInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return sendRequestAndGetResponse(method, objects);
        }
    }
}
