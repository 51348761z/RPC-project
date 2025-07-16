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

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    private String host;
    private int port;
    private RpcClient rpcClient;
    public static final int SIMPLE_RPC_CLIENT = 0;
    public static final int NETTY_RPC_CLIENT = 1;
    public ClientProxy(String host, int port, int choose) {
        switch (choose) {
            case SIMPLE_RPC_CLIENT: {
                rpcClient = new SimpleSocketRpcClient(host, port);
                break;
            }
            case NETTY_RPC_CLIENT: {
                rpcClient = new NettyRpcClient(host, port);
            }
        }
    }
    public ClientProxy (String host, int port) {
        rpcClient = new NettyRpcClient(host, port);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        System.out.println("ClientProxy: invoke method: " + method.getName());
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes()).build();
        RpcResponse response = IOClient.sendRequest(host, port, request);
        return response.getData();
    }
    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
