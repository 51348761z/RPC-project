package wongs.tinyrpc.transport.codec;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.common.model.RpcResponse;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.transport.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@Slf4j
@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        log.info("{}", "MyEncoder encode method called" + msg.getClass().getName());
        if (msg instanceof RpcRequest) {
            out.writeShort(MessageType.RPC_REQUEST.getCode());
        } else if (msg instanceof RpcResponse) {
            out.writeShort(MessageType.RPC_RESPONSE.getCode());
        }
        out.writeShort(serializer.getCode());
        byte[] serializedBytes = serializer.serialize(msg);
        out.writeInt(serializedBytes.length);
        out.writeBytes(serializedBytes);
    }
}
