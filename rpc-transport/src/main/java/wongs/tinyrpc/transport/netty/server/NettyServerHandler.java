package wongs.tinyrpc.transport.netty.server;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.server.provider.ServiceProvider;
import wongs.tinyrpc.core.server.ratelimit.RateLimit;
import wongs.tinyrpc.common.model.RpcResponse;
import wongs.tinyrpc.common.model.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import wongs.tinyrpc.core.trace.TraceIdUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private ServiceProvider serviceProvider;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        Map<String, String> attachments = request.getAttachments();
        if (attachments != null) {
            String traceId = attachments.get(TraceIdUtil.TRACE_ID);
            String spanId = attachments.get(TraceIdUtil.SPAN_ID);
            if (traceId != null) {
                TraceIdUtil.setTraceId(traceId);
                TraceIdUtil.setSpanId(spanId);
            }
        }
       try {
           RpcResponse response = getResponse(request);
           ctx.writeAndFlush(response);
           ctx.close();
       } finally {
           TraceIdUtil.clear();
       }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("An error occurred", cause);
        ctx.close();
    }

    private RpcResponse getResponse(RpcRequest rpcRequest) {
        String interfaceName = rpcRequest.getInterfaceName();
        // Check if the rate limit allows the request
        RateLimit rateLimit = serviceProvider.getRateLimitProvider().getRateLimit(interfaceName);
        if (!rateLimit.getToken()) {
            log.info("{}", "Rate limit exceeded for interface: " + interfaceName);
            return RpcResponse.fail();
        }

        Object service = serviceProvider.getService(interfaceName);
        Method method = null;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object invoke = method.invoke(service, rpcRequest.getParameters());
            return RpcResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            log.error("An error occurred", exception);
            log.info("{}", "Method not found or access denied");
            return RpcResponse.fail();
        }
    }
}
