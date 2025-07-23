package Server.factory;

import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import Server.server.impl.NettyRPCServer;
import Server.server.impl.SimpleRPCServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RpcServerFactory {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = new Properties();
    static {
        try (InputStream input = RpcServerFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + CONFIG_FILE);
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load server configuration!", e);
        }
    }
    public static RpcServer createRpcServer(ServiceProvider serviceProvider) {
        String serverType = PROPERTIES.getProperty("rpc.server.type", "netty").toLowerCase();
        return switch (serverType) {
            case "netty" -> {
                System.out.println("Creating Netty Rpc Server...");
                yield new NettyRPCServer(serviceProvider);
            }
            case "simplesocket" -> {
                System.out.println("Creating Simple Socket Rpc Server...");
                yield new SimpleRPCServer(serviceProvider);
            }
            default -> throw new IllegalArgumentException("Unknown RPC server type in configuration: " + serverType);
        };
    }
    public static int getServerPort() {
        return Integer.parseInt(PROPERTIES.getProperty("rpc.server.port", "9999"));
    }
    public static String getServerHost() {
        return PROPERTIES.getProperty("rpc.server.host", "127.0.0.1");
    }
}

