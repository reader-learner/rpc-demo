package com;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootConfiguration
@ComponentScan(basePackages = "com")
public class ClientApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        Test test = applicationContext.getBean(Test.class);
        System.out.println(test.test());

//        NettyRPCClient nettyRPCClient = new NettyRPCClient();
//        ServiceConfig config = ServiceConfig.builder()
//                .group("test")
//                .version("01")
//                .build();
//        RpcDynamicProxy rpcDynamicProxy = new RpcDynamicProxy(nettyRPCClient, config);
//        ServerAnswer proxy = rpcDynamicProxy.getProxy(ServerAnswer.class);
//        CustomProxy jdkDynamicProxy = new CglibDynamicProxy(nettyRPCClient, config);
//        ServerAnswer proxy = jdkDynamicProxy.getProxy(ServerAnswer.class);
//        System.out.println(proxy.getNum(0));
    }

}

