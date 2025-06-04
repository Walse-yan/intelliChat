package org.example.chatserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ollama.api")
public class OllamaConfig {
    private String url;
    private String model;
}
