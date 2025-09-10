package wongs.tinyrpc.core.server.transport;

public interface RpcServer {
    void start(int port);
    void stop();
}
