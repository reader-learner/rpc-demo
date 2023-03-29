package com.rpc.transport.netty.work;

import com.rpc.provider.ServiceProvider;
import com.rpc.transport.constant.RPCProtocolConstant;
import com.rpc.transport.dto.RPCMessage;
import com.rpc.transport.dto.RPCRequest;
import com.rpc.transport.dto.RPCResponse;
import com.rpc.transport.netty.IdleHandler;
import enums.ErrorEnum;
import enums.ResponseCodeEnum;
import exception.RPCException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class WorkHandler implements Runnable {
    private final ServiceProvider serviceProvider;
    private final Object msg;
    private final ChannelHandlerContext ctx;

    public WorkHandler(Object msg, ChannelHandlerContext ctx, ServiceProvider serviceProvider) {
        this.msg = msg;
        this.ctx = ctx;
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void run() {
        try {
            if (msg instanceof RPCMessage) {
                RPCMessage rpcMessage = (RPCMessage) msg;
                byte messageType = rpcMessage.getMessageType();
                RPCMessage rpcResponse;
                if (messageType == RPCProtocolConstant.HEARTBEAT_REQUEST_TYPE
                        || messageType == RPCProtocolConstant.HEARTBEAT_RESPONSE_TYPE) {
                    rpcResponse = IdleHandler.invoke(rpcMessage, messageType);
                    if (rpcResponse == null) return;
                } else {
                    rpcResponse = RPCMessage.builder().codec(rpcMessage.getCodec()).requestId((rpcMessage.getRequestId()))
                            .compress(rpcMessage.getCompress()).build();
                    RPCRequest rpcRequest = (RPCRequest) rpcMessage.getPayload();
                    Object res = callService(rpcRequest);
                    log.info("server get result: [{}]", res);
                    rpcResponse.setMessageType(RPCProtocolConstant.RESPONSE_TYPE);
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        rpcResponse.setPayload(RPCResponse.success(res, rpcRequest.getRequestId()));
                    } else {
                        log.error("channel is not writable now, message dropped");
                        rpcResponse.setPayload(RPCResponse.fail(ResponseCodeEnum.FAIL));
                    }
                }
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            //释放（release）引用计数对象时，它的引用计数减1.如果引用计数为0，这个引用计数对象会被释放（deallocate），并返回对象池。
            ReferenceCountUtil.release(msg);
        }
    }

    private Object callService(RPCRequest rpcRequest) {
        Object res;
        try {
            String serviceName = rpcRequest.getServiceName();
            Object providerService = serviceProvider.getService(serviceName);
            Method method = providerService.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            res = method.invoke(providerService, rpcRequest.getParameters());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RPCException(ErrorEnum.CALL_SERVICE_ERROR, e.getMessage());
        }
        return res;
    }

}
