package serializer.myCodec;

import common.message.MessageType;
import common.message.RpcRequest;
import common.message.RpcResponse;
import serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        System.out.println("MyEncoder encode method called" + msg.getClass().getName());
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
