package Server.server.impl;

import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.server.work.WorkThread;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRPCServer implements RpcServer {
    private final ThreadPoolExecutor threadPoolExecutor;
    private ServiceProvider serviceProvider;
    public ThreadPoolRPCServer(ServiceProvider serviceProvider) {
        threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                1000, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
        this.serviceProvider = serviceProvider;
    }
    public ThreadPoolRPCServer(ServiceProvider serviceProvider, int corePoolSize, int maximumPoolSize,
                               long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port) {
        System.out.println("Starting server on port " + port);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                threadPoolExecutor.execute(new WorkThread(socket, serviceProvider));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void stop() {
        System.out.println("Stopping server");
    }
}
