package com.studentsbff.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiConfig {

    private String apiKey;
    private String model = "gpt-4o";
    private int maxTokens = 2000;
}
