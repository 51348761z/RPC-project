package Client.proxy;

import Client.IOClient;
import Client.rpcClient.RpcClient;
import Client.rpcClient.impl.NettyRpcClient;
import Client.rpcClient.impl.SimpleSocketRpcClient;
import common.message.RpcRequest;
import common.message.RpcResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientProxy implements InvocationHandler {
    private String host;
    private int port;
    private RpcClient rpcClient;
    public ClientProxy(String host, int port) {
        throw new UnsupportedOperationException("This constructor should not be used directly.");
    }
    public ClientProxy(String host, int port, RpcClient rpcClient) {
        this.host = host;
        this.port = port;
        this.rpcClient = rpcClient;
        System.out.println("Proxy initialized for  host: " + this.host + ", port: " + this.port + ", using client: " + rpcClient.getClass().getName());
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Invoking method: " + method.getName());
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes()).build();
        System.out.println("Sending RPC request to " + host + ":" + port + " for method " + request.getMethodName());
        RpcResponse response = rpcClient.sendRequest(request);
        System.out.println("Received RPC response data: " + response.getData());
        return response.getData();
    }
    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
