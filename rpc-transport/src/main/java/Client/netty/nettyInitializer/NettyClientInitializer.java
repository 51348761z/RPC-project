package Client.netty.nettyInitializer;

import Client.netty.handler.NettyClientHandler; // Imports the custom client-side business logic handler
import common.serializer.myCodec.MyDecoder;
import common.serializer.myCodec.MyEncoder;
import common.serializer.mySerializer.JsonSerializer;
import io.netty.channel.ChannelInitializer; // Netty's base class for initializing a Channel
import io.netty.channel.ChannelPipeline; // Netty's chain of ChannelHandlers
import io.netty.channel.socket.SocketChannel; // Netty's abstraction for a TCP socket connection

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new NettyClientHandler());
    }
}
