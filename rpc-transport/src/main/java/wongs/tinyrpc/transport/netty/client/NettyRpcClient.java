package wongs.tinyrpc.transport.netty.client;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.client.transport.RpcClient;
import wongs.tinyrpc.core.client.discovery.ServiceDiscovery;
import wongs.tinyrpc.registry.zookeeper.ZookeeperServiceDiscovery;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import wongs.tinyrpc.transport.serializer.Impl.JsonSerializer;
import wongs.tinyrpc.transport.serializer.Serializer;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private ServiceDiscovery serviceDiscovery;
    private Serializer serializer;

    public NettyRpcClient() throws InterruptedException {
        this(new ZookeeperServiceDiscovery(), new JsonSerializer());
    }
    public NettyRpcClient(ServiceDiscovery serviceDiscovery, Serializer serializer) throws InterruptedException {
        this.serviceDiscovery = serviceDiscovery;
        this.serializer = serializer;
        this.bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new NettyClientInitializer(this.serializer));
    }
    static {
        eventLoopGroup = new NioEventLoopGroup();
    }
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        log.info("{}", "Sending RPC request: " + request + Thread.currentThread().getStackTrace()[2].getMethodName());
        // get the service address from the service center
        InetSocketAddress address = serviceDiscovery.serviceDiscovery(request.getInterfaceName());
        if (address == null) {
            throw new RuntimeException("Service not found: " + request.getInterfaceName());
        }
        String host = address.getHostName();
        int port = address.getPort();
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();

            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();

            log.info("{}", response);
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to send request: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        eventLoopGroup.shutdownGracefully();
        log.info("NettyRpcClient eventLoopGroup shut down.");
    }
}
