package Client.netty.nettyInitializer;

import Client.netty.handler.NettyClientHandler; // Imports the custom client-side business logic handler
import io.netty.channel.ChannelInitializer; // Netty's base class for initializing a Channel
import io.netty.channel.ChannelPipeline; // Netty's chain of ChannelHandlers
import io.netty.channel.socket.SocketChannel; // Netty's abstraction for a TCP socket connection
import io.netty.handler.codec.LengthFieldBasedFrameDecoder; // Decoder for frame delimiting based on a length field
import io.netty.handler.codec.LengthFieldPrepender; // Encoder for prepending a length field to messages
import io.netty.handler.codec.serialization.ClassResolver; // Used by ObjectDecoder to resolve class names
import io.netty.handler.codec.serialization.ObjectDecoder; // Decoder for Java objects
import io.netty.handler.codec.serialization.ObjectEncoder; // Encoder for Java objects

/**
 * ClientInitializer is a ChannelInitializer that configures the ChannelPipeline
 * for a Netty client's SocketChannel.
 * When a new client connection is established, this initializer sets up the
 * sequence of handlers responsible for data encoding, decoding, framing,
 * and handling of business logic.
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * This method is called when a new Channel is registered to its EventLoop.
     * It's used to configure the Channel's ChannelPipeline by adding various ChannelHandlers.
     *
     * @param ch The newly created and registered SocketChannel
     * @throws Exception if an error occurs during initialization
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // Get the Channel's pipeline, where handlers will process data in the order they are added.
        ChannelPipeline pipeline = ch.pipeline();

        // 1. **Framing Handlers (for reliable data transmission)**
        // LengthFieldBasedFrameDecoder: An inbound decoder to solve TCP's sticky packet/half-packet issues.
        // It reads a length field from the incoming byte stream to determine the boundary of each complete frame.
        // Parameters explanation:
        // - Integer.MAX_VALUE: The maximum length of a frame.
        // - 0: The offset of the length field (length field starts at byte 0 of the frame).
        // - 4: The length of the length field itself (here, assuming 4 bytes for the length field, indicating the total frame length).
        // - 0: The adjustment amount to add to the length field value (usually 0, meaning the length field value is the actual frame length).
        // - 4: The number of bytes to strip from the decoded frame (here, we strip the 4-byte length field itself).
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)
        );

        // LengthFieldPrepender: An outbound encoder that prepends a length field to the actual data.
        // This handler works in conjunction with LengthFieldBasedFrameDecoder on the receiving side
        // to ensure the sent data can be correctly framed.
        // Parameter explanation:
        // - 4: The number of bytes for the length field (a 4-byte length field will be added before the data).
        pipeline.addLast(new LengthFieldPrepender(4));

        // 2. **Serialization/Deserialization Handlers (for object conversion)**
        // ObjectEncoder: An outbound encoder that serializes Java objects into a byte stream for sending.
        // It converts Java objects (that implement Serializable) into ByteBuf.
        pipeline.addLast(new ObjectEncoder());
        // ObjectDecoder: An inbound decoder that deserializes incoming byte streams back into Java objects.
        // It relies on a ClassResolver to resolve class names and load corresponding Class objects.
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            // The resolve method is called by ObjectDecoder when it needs to deserialize an object,
            // to find the Class object corresponding to a given class name string.
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                // Uses Class.forName(className) to load the Class object for the specified class name.
                // Note: This can be a potential security risk (e.g., deserialization vulnerabilities)
                // if 'className' can be controlled by a malicious client.
                return Class.forName(className);
            }
        }));

        // 3. **Business Logic Handler (for application-specific processing)**
        // NettyClientHandler: The custom business logic handler for the client.
        // It's responsible for processing the RpcResponse messages that have been
        // successfully decoded, typically extracting the RPC call result.
        pipeline.addLast(new NettyClientHandler());
    }
}
