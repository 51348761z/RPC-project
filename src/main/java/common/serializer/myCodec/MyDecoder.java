package common.serializer.myCodec;

import common.message.MessageType;
import common.serializer.mySerializer.Serializer;
import common.serializer.mySerializer.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        short messageType = in.readShort();
        if (messageType != MessageType.RPC_REQUEST.getType() && messageType != MessageType.RPC_RESPONSE.getType()) {
            System.out.println("Unsupported message type: " + messageType);
        }
        short serializerType = in.readShort();
        Serializer serializer = Serializer.createSerializer(SerializerType.fromValue(serializerType));
        if (serializer == null) {
            throw new RuntimeException("Unsupported serializer type: " + serializerType);
        }
        // Read the length of the message
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object deserialize = serializer.deserialize(bytes, MessageType.fromValue(messageType));
        out.add(deserialize);
    }
}
