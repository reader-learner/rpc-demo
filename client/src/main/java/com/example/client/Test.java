package com.example.client;

import com.rpc.annotation.RPCAutowire;
import example.ServerAnswer;
import org.springframework.stereotype.Component;

@Component
public class Test {

    @RPCAutowire(serverName = "server", group = "test", version = "01")
    private ServerAnswer hiService;

    public int test() {
        return hiService.getNum(0);
    }
}
