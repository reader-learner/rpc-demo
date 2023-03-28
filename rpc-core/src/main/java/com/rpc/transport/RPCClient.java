package com.rpc.transport;

import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.RPCResponse;
import enums.CompressTypeEnum;
import enums.SerializationEnum;

public interface RPCClient {
    RPCResponse<Object> sendRequest(RPCRequest request);
    RPCResponse<Object> sendRequest(RPCRequest request, SerializationEnum serializationEnum);
    RPCResponse<Object> sendRequest(RPCRequest request, CompressTypeEnum compressTypeEnum);
    RPCResponse<Object> sendRequest(RPCRequest request, SerializationEnum serializationEnum, CompressTypeEnum compressTypeEnum);

}
