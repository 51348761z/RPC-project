package wongs.tinyrpc.transport.codec;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.transport.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class MyDecoder extends ByteToMessageDecoder {
    private static final int HARDER_LENGTH = 8;
    private Serializer serializer;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("{}", "MyDecoder called......");
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
}
