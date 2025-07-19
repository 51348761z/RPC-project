package Client.rpcClient.impl;

import Client.netty.nettyInitializer.NettyClientInitializer;
import Client.rpcClient.RpcClient;
import Client.serviceCenter.ServiceCenter;
import Client.serviceCenter.ZookeeperServiceCenter;
import common.message.RpcRequest;
import common.message.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

public class NettyRpcClient implements RpcClient {
    private final String host;
    private final int port;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private ServiceCenter serviceCenter;

    public NettyRpcClient() {
        this.serviceCenter = new ZookeeperServiceCenter();
    }
    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new NettyClientInitializer());
    }
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();

            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();

            System.out.println(response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
