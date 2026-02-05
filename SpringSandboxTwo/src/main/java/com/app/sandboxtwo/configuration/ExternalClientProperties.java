package com.app.sandboxtwo.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "external")
public class ExternalClientProperties {
    private List<ClientConfig> clients;

    @Data
    public static class ClientConfig {
        private String id;
        private String name;
        private String url;
        private String apiKey;
    }
}
