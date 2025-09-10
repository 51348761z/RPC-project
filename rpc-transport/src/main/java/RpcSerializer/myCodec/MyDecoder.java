package RpcSerializer.myCodec;

import RpcCommon.RpcMessage.MessageType;
import RpcSerializer.mySerializer.Serializer;
import RpcSerializer.mySerializer.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {
    private static final int HARDER_LENGTH = 8;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyDecoder called......");
        if (in.readableBytes() < HARDER_LENGTH) {
            return;
        }

        in.markReaderIndex(); // Mark the current reader index

        // Read the message type
        short messageType = in.readShort();
        if (!isValidMessageType(messageType)) {
            in.resetReaderIndex();
            return;
        }

        // Read the serializer type and create the serializer
        short serializerType = in.readShort();
        Serializer serializer = this.createSerializer(serializerType);

        // Read the length of the message
        int messageLength = in.readInt();
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex(); // Reset to the marked index if not enough bytes are available
            return;
        }
        // Read the message bytes and deserialize
        byte[] messageBytes = new byte[messageLength];
        in.readBytes(messageBytes);
        Object deserialize = serializer.deserialize(messageBytes, MessageType.fromValue(messageType));

        out.add(deserialize);
    }

    private boolean isValidMessageType(short messageType) {
        return messageType == MessageType.RPC_REQUEST.getCode() || messageType == MessageType.RPC_RESPONSE.getCode();
    }

    private Serializer createSerializer(short serializerType) {
        Serializer serializer = Serializer.createSerializer(SerializerType.fromValue(serializerType));
        if (serializer == null) {
            throw new RuntimeException("Unsupported serializer type: " + serializerType);
        }
        return serializer;
    }
}
