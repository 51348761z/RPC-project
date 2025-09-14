package wongs.tinyrpc.example.server;

import io.opentelemetry.api.trace.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import wongs.tinyrpc.example.config.OtelConfiguration;
import wongs.tinyrpc.example.config.RpcFramworkConfig;
import wongs.tinyrpc.core.server.provider.ServiceProvider;
import wongs.tinyrpc.core.server.transport.RpcServer;
import wongs.tinyrpc.common.service.UserService;
import wongs.tinyrpc.example.service.UserServiceImpl;

@Slf4j
public class TestServer {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RpcFramworkConfig.class, OtelConfiguration.class);
        ServiceProvider serviceProvider = context.getBean(ServiceProvider.class);
        RpcServer rpcServer = context.getBean(RpcServer.class);
        Environment environment = context.getEnvironment();
        Tracer tracer = context.getBean(Tracer.class);

        UserService userService = new UserServiceImpl();
        boolean retryEnabled = environment.getProperty("tinyrpc.retry." + userService.getClass().getName().toLowerCase() + ".enabled", Boolean.class, environment.getProperty("tinyrpc.retry.default-enabled", Boolean.class, false));
        serviceProvider.provideServiceInterface(userService, retryEnabled);

        log.info("{}", "Server is starting on " + serviceProvider.getHost() + ":" + serviceProvider.getPort());
        rpcServer.start(serviceProvider.getPort());
    }
}
