package wongs.tinyrpc.transport.netty.server;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.server.provider.ServiceProvider;
import wongs.tinyrpc.core.server.transport.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import wongs.tinyrpc.transport.serializer.Serializer;

@Slf4j
public class NettyRpcServer implements RpcServer {
    private ServiceProvider serviceProvider;
    private ChannelFuture channelFuture;
    private Serializer serializer;
    public NettyRpcServer(ServiceProvider serviceProvider, Serializer serializer) {
        this.serviceProvider = serviceProvider;
        this.serializer = serializer;
        log.info("{}", "NettyRpcServer initialized with serializer: " + serializer.getClass().getName());
    }

    @Override
    public void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).childHandler(new NettyServerInitializer(serviceProvider, serializer));
            channelFuture = serverBootstrap.bind(port).sync();
            log.info("RPC server binded on port {}", port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("RPC server interrupted :{}", e.getMessage(), e);
        } finally {
            shutdown(bossGroup, workGroup);
            log.info("RPC server event loop groups shut down.");
        }
    }

    @Override
    public void stop() {
        if (channelFuture != null) {
            try {
                channelFuture.channel().close().sync();
                log.info("RPC server stopped.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Error while stopping the RPC server", e);
            }
        } else {
            log.warn("ChannelFuture is null, server was not started or already stopped.");
        }
    }

    private void shutdown(NioEventLoopGroup bossGroup, NioEventLoopGroup workGroup) {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully().syncUninterruptibly();
        }
    }
}
