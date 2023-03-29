package com.example.client;

import com.rpc.annotation.RpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackages = "com.rpc")
@RpcScan(basePackage = "com")
public class ClientApplication {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext run = SpringApplication.run(ClientApplication.class, args);
        Test bean = run.getBean(Test.class);
        while (true) {
            TimeUnit.SECONDS.sleep(4);
            System.out.println(bean.test());
        }
    }

}
