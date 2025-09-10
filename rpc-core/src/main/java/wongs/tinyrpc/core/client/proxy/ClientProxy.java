package wongs.tinyrpc.core.client.proxy;

import wongs.tinyrpc.core.client.breaker.CircuitBreaker;
import wongs.tinyrpc.core.client.breaker.CircuitBreakerProvider;
import wongs.tinyrpc.core.client.transport.RpcClient;
//import Client.rpcClient.impl.NettyRpcClient;
import wongs.tinyrpc.core.client.discovery.ServiceCenter;
import wongs.tinyrpc.core.client.retry.RetryStrategy;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;
    private CircuitBreakerProvider circuitBreakerProvider;
    private RetryStrategy retryStrategy;

    public ClientProxy(RpcClient rpcClient, ServiceCenter serviceCenter, CircuitBreakerProvider circuitBreakerProvider, RetryStrategy retryStrategy) throws InterruptedException {
        this.rpcClient = rpcClient;
        this.serviceCenter = serviceCenter;
        this.circuitBreakerProvider = circuitBreakerProvider;
        this.retryStrategy = retryStrategy;
    }
    public ClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        System.out.println("Proxy initialized using client: " + rpcClient.getClass().getName());
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Invoking method: " + method.getName());
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes()).build();
        System.out.println("Sending RPC request for method " + request.getMethodName());

        // Check if circuit breaker is open for the service
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        if (!circuitBreaker.allowRequest()) {
            System.out.println("Circuit breaker is open for method: " + method.getName());
            return null;
        }

        RpcResponse response;
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            response = retryStrategy.excute(request, rpcClient);
        } else {
            response = rpcClient.sendRequest(request);
        }
        System.out.println("Received RPC response data: " + response.getData());
        return response.getData();
    }
    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
