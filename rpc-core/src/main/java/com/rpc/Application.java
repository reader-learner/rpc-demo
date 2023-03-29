package com.rpc;

import com.rpc.properties.RPCProperties;
import com.rpc.transport.netty.server.NettyRPCServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties(RPCProperties.class)
@Slf4j
public class Application {

    public Application(){}
    public static void main(String[] args) {// 等待Spring容器初始化完成
        ApplicationContext run = SpringApplication.run(Application.class, args);
        NettyRPCServer bean = run.getBean(NettyRPCServer.class);
        bean.start();


    }
}

