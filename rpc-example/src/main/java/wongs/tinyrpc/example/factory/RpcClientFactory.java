package wongs.tinyrpc.example.factory;

import wongs.tinyrpc.core.client.transport.RpcClient;
import wongs.tinyrpc.transport.netty.client.NettyRpcClient;
import wongs.tinyrpc.transport.socket.SimpleSocketRpcClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RpcClientFactory {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = new Properties();
    static {
        try (InputStream input = RpcClientFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + CONFIG_FILE);
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load client configuration!", e);
        }
    }

    public static RpcClient createRpcClient() throws InterruptedException {
        String clientType = PROPERTIES.getProperty("rpc.client.type", "netty").toLowerCase();
        String host = PROPERTIES.getProperty("rpc.server.host", "127.0.0.1");
        int port = Integer.parseInt(PROPERTIES.getProperty("rpc.server.port", "9999"));
        return switch (clientType) {
            case "netty" -> {
                System.out.println("Creating Netty RPC Client for " + host + ":" + port);
                yield new NettyRpcClient();
                }
            case "simplesoket" -> {
                System.out.println("Creating Simple Soket RPC Client for " + host + ":" + port);
                yield new SimpleSocketRpcClient(host, port);
            }
            default -> throw new IllegalArgumentException("Unknown RPC client type in configuration: " + clientType);
        };
    }
    public static String getRpcServerHost() {
        return PROPERTIES.getProperty("rpc.server.host", "127.0.0.1");
    }

    public static int getRpcServerPort() {
        String portStr = PROPERTIES.getProperty("rpc.server.port", "9999");
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number in config. Using default port 9999. Error: " + e.getMessage());
            return 9999;
        }
    }
}
