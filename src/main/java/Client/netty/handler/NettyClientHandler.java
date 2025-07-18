package Client.netty.handler;

import common.message.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    // Please keep in mind that #channelRead0(ChannelHandlerContext, I) will be renamed to messageReceived(ChannelHandlerContext, I) in 5.0.
    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception{
    protected void messageReceived(ChannelHandlerContext ctx, RpcResponse response) throws Exception{
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
        ctx.channel().attr(key).set(response);
        ctx.channel().close();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
