package com.rpc.transport.dto;

import enums.ResponseCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RPCResponse<T> {
    private String requestId;
    private Integer statusCode;
    private String message;
    private T data;

    public static <T> RPCResponse<T> success(T data, String requestId) {
        return (RPCResponse<T>) RPCResponse.builder().data(data).requestId(requestId)
                .statusCode(ResponseCodeEnum.SUCCESS.getCode()).message(ResponseCodeEnum.SUCCESS.getMessage())
                .build();
    }

    public static <T> RPCResponse<T> fail(ResponseCodeEnum responseCodeEnum) {
        return (RPCResponse<T>) RPCResponse.builder().message(responseCodeEnum.getMessage())
                .statusCode(responseCodeEnum.getCode()).build();
    }
}
