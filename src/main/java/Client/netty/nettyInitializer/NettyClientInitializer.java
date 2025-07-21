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

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // Get the Channel's pipeline, where handlers will process data in the order they are added.
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)
        );
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));

        pipeline.addLast(new NettyClientHandler());
    }
}
