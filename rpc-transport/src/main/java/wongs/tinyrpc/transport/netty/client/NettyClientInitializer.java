package wongs.tinyrpc.transport.netty.client;

import lombok.AllArgsConstructor;
import wongs.tinyrpc.transport.codec.MyEncoder;
import wongs.tinyrpc.transport.codec.MyDecoder;
import wongs.tinyrpc.transport.serializer.Impl.JsonSerializer;
import io.netty.channel.ChannelInitializer; // Netty's base class for initializing a Channel
import io.netty.channel.ChannelPipeline; // Netty's chain of ChannelHandlers
import io.netty.channel.socket.SocketChannel; // Netty's abstraction for a TCP socket connection
import wongs.tinyrpc.transport.serializer.Serializer;

@AllArgsConstructor
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    private Serializer serializer;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(serializer));
        pipeline.addLast(new NettyClientHandler());
    }
}
