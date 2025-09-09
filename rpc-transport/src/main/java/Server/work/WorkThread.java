package Server.work;

import Server.provider.ServiceProvider;
import common.message.RpcRequest;
import common.message.RpcResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

@AllArgsConstructor
public class WorkThread implements Runnable {
    private Socket socket;
    private ServiceProvider serviceProvider;

    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            RpcRequest rpcRequest= (RpcRequest) ois.readObject();
            RpcResponse rpcResponse = getResponse(rpcRequest);
            oos.writeObject(rpcResponse);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private RpcResponse getResponse(RpcRequest rpcRequest) {
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serviceProvider.getService(interfaceName);
        Method method = null;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object invoke = method.invoke(service, rpcRequest.getParameters());
            return RpcResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("Method not found or access denied");
            return RpcResponse.fail();
        }
    }
}
