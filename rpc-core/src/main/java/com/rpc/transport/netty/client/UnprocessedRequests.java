package com.rpc.transport.netty.client;

import com.rpc.transport.dto.RPCResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理完成的请求（等待服务器响应）
 */
public class UnprocessedRequests {
    public static final Map<String, CompletableFuture<RPCResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RPCResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete(RPCResponse<Object> response) {
        CompletableFuture<RPCResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        } else {
            throw new IllegalArgumentException("未知响应");
        }
    }

}
