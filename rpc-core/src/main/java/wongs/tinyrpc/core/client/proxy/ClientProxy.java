package wongs.tinyrpc.core.client.proxy;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.client.breaker.CircuitBreaker;
import wongs.tinyrpc.core.client.breaker.CircuitBreakerProvider;
import wongs.tinyrpc.core.client.transport.RpcClient;
import wongs.tinyrpc.core.client.discovery.ServiceDiscovery;
import wongs.tinyrpc.core.client.retry.RetryStrategy;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;
import wongs.tinyrpc.core.trace.TraceIdUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;
    private ServiceDiscovery serviceDiscovery;
    private CircuitBreakerProvider circuitBreakerProvider;
    private RetryStrategy retryStrategy;
    private Tracer tracer;

    public ClientProxy(RpcClient rpcClient, ServiceDiscovery serviceDiscovery, CircuitBreakerProvider circuitBreakerProvider, RetryStrategy retryStrategy, Tracer tracer) throws InterruptedException {
        this.rpcClient = rpcClient;
        this.serviceDiscovery = serviceDiscovery;
        this.circuitBreakerProvider = circuitBreakerProvider;
        this.retryStrategy = retryStrategy;
        this.tracer = tracer;
    }
    public ClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        log.info("{}", "Proxy initialized using client: " + rpcClient.getClass().getName());
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String traceId = TraceIdUtil.getTraceId();
        if (traceId == null) { // If no traceId exists, generate a new one
            traceId = TraceIdUtil.getTraceId();
            TraceIdUtil.setTraceId(traceId);
        }
        String spanId = TraceIdUtil.getNextId(); // Generate a new spanId for this method call
        TraceIdUtil.setSpanId(spanId);

        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .build();

        // Attach traceId and spanId to the request
        Map<String, String> attachments = new HashMap<>();
        attachments.put(TraceIdUtil.TRACE_ID, traceId);
        attachments.put(TraceIdUtil.SPAN_ID, spanId);
        request.setAttachments(attachments);
        log.info("{}", "Sending RPC request for method " + request.getMethodName());

        // Check if circuit breaker is open for the service
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        if (!circuitBreaker.allowRequest()) {
            log.info("{}", "Circuit breaker is open for method: " + method.getName());
            return null;
        }

        RpcResponse response;
        Span span = tracer.spanBuilder(request.getInterfaceName()).startSpan();
        try {
            span.addEvent("Request processing started.");
            if (serviceDiscovery.checkRetry(request.getInterfaceName())) {
                response = retryStrategy.execute(request, rpcClient);
            } else {
                response = rpcClient.sendRequest(request);
            }
            span.addEvent("Request processing completed.");
        } finally {
            span.end();
            TraceIdUtil.clear();
        }
        log.info("{}", "Received RPC response data: " + response.getData());
        return response.getData();
    }
    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
