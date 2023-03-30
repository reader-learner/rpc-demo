package com.rpc.transport.netty.codec;

import com.rpc.compress.Compress;
import com.rpc.serialize.Serializer;
import com.rpc.transport.constant.RPCProtocolConstant;
import com.rpc.transport.dto.RPCMessage;
import enums.CompressTypeEnum;
import enums.ErrorEnum;
import enums.SerializationEnum;
import exception.RPCException;
import extension.ExtensionLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 数据包
 * <p>
 * 4byte       4byte       2byte       1byte       1byte        1byte       4byte
 * +------------+------------+---------+-----------+-------------+-----------+------------+
 * |    魔术位   |  整体长度   |  头长度  |   协议版本  |   消息类型   |  序列化方式 |    请求ID   |
 * +-----------+------------+---------+-----------+-------------+-----------+-------------+
 * |                                     协议头扩展字段                                     |
 * +--------------------------------------------------------------------------------------+
 * |                                                                                      |
 * |                                     payload                                          |
 * |                                                                                      |
 * +--------------------------------------------------------------------------------------+
 */
@Slf4j
public class NettyMessageEncoder extends MessageToByteEncoder<RPCMessage> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RPCMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        try {
            // 魔数
            byteBuf.writeBytes(RPCProtocolConstant.MAGIC_NUMBER);
            // 空出整体长度字段
            // 整体长度的偏移量
            int fullLengthOffset = byteBuf.writerIndex();
            byteBuf.writerIndex(fullLengthOffset + RPCProtocolConstant.FULL_LENGTH_FIELD_LENGTH);
            // 头长度
            byteBuf.writeShort(RPCProtocolConstant.HEAD_LENGTH);
            // 协议版本
            byteBuf.writeByte(RPCProtocolConstant.VERSION);
            // 消息类型
            byte messageType = rpcMessage.getMessageType();
            byteBuf.writeByte(messageType);
            // 序列化方式
            byteBuf.writeByte(rpcMessage.getCodec());
            // 请求ID
            byteBuf.writeInt(rpcMessage.getRequestId());
            byte[] data = null;
            // 心跳请求和响应交给 NettyClientHandler 和 NettyServerHandler 处理
            if (messageType != RPCProtocolConstant.HEARTBEAT_REQUEST_TYPE && messageType != RPCProtocolConstant.HEARTBEAT_RESPONSE_TYPE) {
                // 序列化
                String codecName = SerializationEnum.lookup(rpcMessage.getCodec());
                log.info("codec name: [{}]", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName);
                Object payload = rpcMessage.getPayload();
                data = serializer.serialize(payload);
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                if (compressName != null) {
                    Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                            .getExtension(compressName);
                    data = compress.compress(data);
                }
            }
            // 写 payload
            if (data != null) {
                byteBuf.writeBytes(data);
            }
            // 记录末尾下标，写完整体长度后恢复
            int tailWriterIndex = byteBuf.writerIndex();
            int fullLength = (tailWriterIndex - fullLengthOffset) + RPCProtocolConstant.MAGIC_NUMBER.length;
            // 调正写指针下标，写整体长度
            byteBuf.writerIndex(fullLengthOffset).writeInt(fullLength);
            // 恢复写指针到末尾
            byteBuf.writerIndex(tailWriterIndex);
        } catch (Exception e) {
            log.error("encode request error", e);
            throw new RPCException(ErrorEnum.ENCODE_FRAME_ERROR, e.getMessage());
        }
    }
}
