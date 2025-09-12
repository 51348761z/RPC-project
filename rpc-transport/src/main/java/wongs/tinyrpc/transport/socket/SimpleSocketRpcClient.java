package wongs.tinyrpc.transport.socket;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.client.transport.RpcClient;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class SimpleSocketRpcClient implements RpcClient {
    private String host;
    private int port;
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(request);
            oos.flush();

            RpcResponse response = (RpcResponse) ois.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            log.error("An error occurred", e);
            return null;
        }
    }

    @Override
    public void close() {
        // No persistent connection to close in this simple implementation
    }
}
