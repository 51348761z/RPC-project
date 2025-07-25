package Client.proxy;

import Client.rpcClient.RpcClient;
import Client.rpcClient.impl.NettyRpcClient;
import Client.serviceCenter.ServiceCenter;
import Client.serviceCenter.ZookeeperServiceCenter;
import Client.retry.guavaRetry;
import common.message.RpcRequest;
import common.message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;

    public ClientProxy() throws InterruptedException {
        serviceCenter = new ZookeeperServiceCenter();
        rpcClient = new NettyRpcClient(serviceCenter);
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
        RpcResponse response;
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            response = new guavaRetry().sendServiceWithRetry(request, rpcClient);
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
