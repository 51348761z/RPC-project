package wongs.tinyrpc.example.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RetryConfig {
    private static Properties properties = new Properties();
    static {
        try (InputStream is = RetryConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                System.err.println("Sorry, unable to find config.properties");
            }
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isRetryEnabled(String serviceName) {
        String key = "retry.enabled." + serviceName.toLowerCase();
        return Boolean.parseBoolean(properties.getProperty(key, properties.getProperty("retry.enabled.default", "false")));
    }
}
