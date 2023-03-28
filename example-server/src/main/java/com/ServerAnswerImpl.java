package com;

import com.rpc.annotation.RPCService;
import example.ServerAnswer;
@RPCService(group = "test",version = "01")
public class ServerAnswerImpl implements ServerAnswer {
    @Override
    public int getNum(int num) {
        return num+1;
    }

}
