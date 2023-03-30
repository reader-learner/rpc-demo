package com.example.client;

import com.rpc.annotation.RpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "com.rpc")
@RpcScan(basePackage = "com")
public class ClientApplication {

    public static void main(String[] args) {
        ApplicationContext run = SpringApplication.run(ClientApplication.class, args);
        Test bean = run.getBean(Test.class);
        System.out.println(bean.test());
    }

}
