package com.studentsbff.ai;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAiProvider implements LLMProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiProvider.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final OpenAiConfig config;

    public OpenAiProvider(OpenAiConfig config) {
        this(config, new RestTemplate());
    }

    OpenAiProvider(OpenAiConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String complete(String systemPrompt, String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getApiKey());

        Map<String, Object> requestBody =
                Map.of(
                        "model", config.getModel(),
                        "max_tokens", config.getMaxTokens(),
                        "messages",
                                List.of(
                                        Map.of("role", "system", "content", systemPrompt),
                                        Map.of("role", "user", "content", userMessage)),
                        "response_format", Map.of("type", "json_object"));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        log.debug("Sending request to OpenAI API with model: {}", config.getModel());

        Map<String, Object> response =
                restTemplate.postForObject(OPENAI_API_URL, request, Map.class);

        if (response == null) {
            throw new RuntimeException("OpenAI API returned null response");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("OpenAI API returned no choices");
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}
