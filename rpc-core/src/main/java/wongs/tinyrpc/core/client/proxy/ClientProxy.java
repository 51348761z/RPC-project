package wongs.tinyrpc.core.client.proxy;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.client.breaker.CircuitBreaker;
import wongs.tinyrpc.core.client.breaker.CircuitBreakerProvider;
import wongs.tinyrpc.core.client.transport.RpcClient;
import wongs.tinyrpc.core.client.discovery.ServiceDiscovery;
import wongs.tinyrpc.core.client.retry.RetryStrategy;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;
    private ServiceDiscovery serviceDiscovery;
    private CircuitBreakerProvider circuitBreakerProvider;
    private RetryStrategy retryStrategy;

    public ClientProxy(RpcClient rpcClient, ServiceDiscovery serviceDiscovery, CircuitBreakerProvider circuitBreakerProvider, RetryStrategy retryStrategy) throws InterruptedException {
        this.rpcClient = rpcClient;
        this.serviceDiscovery = serviceDiscovery;
        this.circuitBreakerProvider = circuitBreakerProvider;
        this.retryStrategy = retryStrategy;
    }
    public ClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        log.info("{}", "Proxy initialized using client: " + rpcClient.getClass().getName());
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("{}", "Invoking method: " + method.getName());
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes()).build();
        log.info("{}", "Sending RPC request for method " + request.getMethodName());

        // Check if circuit breaker is open for the service
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        if (!circuitBreaker.allowRequest()) {
            log.info("{}", "Circuit breaker is open for method: " + method.getName());
            return null;
        }

        RpcResponse response;
        if (serviceDiscovery.checkRetry(request.getInterfaceName())) {
            response = retryStrategy.execute(request, rpcClient);
        } else {
            response = rpcClient.sendRequest(request);
        }
        log.info("{}", "Received RPC response data: " + response.getData());
        return response.getData();
    }
    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
