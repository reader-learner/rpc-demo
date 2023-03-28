package com;

import com.rpc.config.ServiceConfig;
import com.rpc.transport.RPCServer;
import com.rpc.transport.netty.server.NettyRPCServer;
import example.ServerAnswer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan(basePackages = "com.rpc")
public class ServerApplication {
    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ServerApplication.class);
        NettyRPCServer nettyRpcServer = applicationContext.getBean(NettyRPCServer.class);
        // Register service manually
        ServerAnswer serverAnswer = new ServerAnswerImpl();
        ServiceConfig rpcServiceConfig = ServiceConfig.builder()
                .group("test").version("01").service(serverAnswer).build();
        nettyRpcServer.publishService(rpcServiceConfig);
        nettyRpcServer.start();
    }


}
