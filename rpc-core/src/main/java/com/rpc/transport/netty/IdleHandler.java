package com.rpc.transport.netty;

import com.rpc.transport.constant.RPCProtocolConstant;
import com.rpc.transport.dto.RPCMessage;
import enums.CompressTypeEnum;
import enums.SerializationEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdleHandler {
    public static RPCMessage invoke(RPCMessage rpcMessage, byte messageType) {
        if (messageType == RPCProtocolConstant.HEARTBEAT_RESPONSE_TYPE) {
            log.info("receive heart response: [{}]", rpcMessage.getPayload());
            return null;
        }
        RPCMessage rpcResponse = RPCMessage.builder().codec(rpcMessage.getCodec()).requestId((rpcMessage.getRequestId()))
                .compress(rpcMessage.getCompress()).build();
        log.info("receive heart request: [{}]", rpcMessage.getPayload());
        rpcResponse.setMessageType(RPCProtocolConstant.HEARTBEAT_RESPONSE_TYPE);
        rpcResponse.setPayload(RPCProtocolConstant.PONG);
        return rpcResponse;

    }
    public static RPCMessage sendIdleRequest(){
        RPCMessage rpcMessage = new RPCMessage();
        rpcMessage.setCodec(SerializationEnum.PROTOSTUFF.getCode());
        rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
        rpcMessage.setMessageType(RPCProtocolConstant.HEARTBEAT_REQUEST_TYPE);
        rpcMessage.setPayload(RPCProtocolConstant.PING);
        return rpcMessage;
    }


}
