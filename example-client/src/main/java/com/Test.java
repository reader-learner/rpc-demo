package com;

import com.rpc.annotation.RPCAutowire;
import example.ServerAnswer;
import org.springframework.stereotype.Component;

@Component
public class Test {

    @RPCAutowire(group = "test", version = "01")
    private ServerAnswer hiService;

    public int test() {
        return hiService.getNum(0);
    }
}
