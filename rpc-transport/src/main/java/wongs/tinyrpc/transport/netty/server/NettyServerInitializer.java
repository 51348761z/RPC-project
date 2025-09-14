package wongs.tinyrpc.transport.netty.server;

import io.opentelemetry.api.trace.Tracer;
import wongs.tinyrpc.core.server.provider.ServiceProvider;
import wongs.tinyrpc.transport.codec.MyDecoder;
import wongs.tinyrpc.transport.codec.MyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import wongs.tinyrpc.transport.serializer.Serializer;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    private Serializer serializer;
    private Tracer tracer;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new MyDecoder(serializer));
        pipeline.addLast(new MyEncoder(serializer));
        pipeline.addLast(new NettyServerHandler(serviceProvider, tracer));
    }
}
