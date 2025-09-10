package wongs.tinyrpc.transport.netty.server;

import wongs.tinyrpc.core.server.provider.ServiceProvider;
import wongs.tinyrpc.transport.serializer.Impl.JsonSerializer;
import wongs.tinyrpc.transport.codec.MyDecoder;
import wongs.tinyrpc.transport.codec.MyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new NettyServerHandler(serviceProvider));
    }
}
