package com.example.server;

import com.rpc.annotation.RpcScan;
import com.rpc.transport.netty.server.NettyRPCServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com")
@RpcScan(basePackage = "com.example.server")
public class ServerApplication {

    @Autowired
    private NettyRPCServer nettyRPCServer;

    public static void main(String[] args) {
        // Register service via annotation
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ServerApplication.class, args);

        applicationContext.getBean(ServerApplication.class).test();
    }

    void test() {
//        nettyRPCServer.postProcessAfterInitialization()
        nettyRPCServer.start();
    }

}
