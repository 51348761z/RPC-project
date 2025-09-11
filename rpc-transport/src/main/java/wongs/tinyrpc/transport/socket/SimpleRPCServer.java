package wongs.tinyrpc.transport.socket;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.server.provider.ServiceProvider;
import wongs.tinyrpc.core.server.transport.RpcServer;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class SimpleRPCServer implements RpcServer {
    private ServiceProvider serviceProvider;
    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("{}", "Server started on port " + port + "......");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }
        } catch (IOException e) {
            log.error("An error occurred", e);
        }
    }
    @Override
    public void stop() {
        log.info("{}", "Server stopped......");
    }
}
