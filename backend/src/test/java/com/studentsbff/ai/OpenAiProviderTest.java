package com.studentsbff.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class OpenAiProviderTest {

    @Mock
    private RestTemplate restTemplate;

    private OpenAiProvider openAiProvider;

    @BeforeEach
    void setUp() {
        OpenAiConfig config = new OpenAiConfig();
        config.setApiKey("test-api-key");
        config.setModel("gpt-4o");
        config.setMaxTokens(2000);
        openAiProvider = new OpenAiProvider(config, restTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSendCorrectRequestFormat() {
        Map<String, Object> response =
                Map.of(
                        "choices",
                        List.of(Map.of("message", Map.of("content", "{\"events\":[]}"))));
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        openAiProvider.complete("system prompt", "user message");

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate)
                .postForObject(
                        eq("https://api.openai.com/v1/chat/completions"),
                        captor.capture(),
                        eq(Map.class));

        HttpEntity<Map<String, Object>> captured = captor.getValue();
        Map<String, Object> body = captured.getBody();
        assertThat(body).containsKey("model");
        assertThat(body).containsKey("messages");
        assertThat(body).containsKey("response_format");
        assertThat(body.get("model")).isEqualTo("gpt-4o");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldParseApiResponse() {
        String expectedContent = "{\"events\":[{\"title\":\"Math Exam\"}]}";
        Map<String, Object> response =
                Map.of("choices", List.of(Map.of("message", Map.of("content", expectedContent))));
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        String result = openAiProvider.complete("system", "user");

        assertThat(result).isEqualTo(expectedContent);
    }

    @Test
    void shouldThrowOnNullResponse() {
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> openAiProvider.complete("system", "user"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("null response");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldIncludeSystemAndUserMessages() {
        Map<String, Object> response =
                Map.of(
                        "choices",
                        List.of(Map.of("message", Map.of("content", "{\"events\":[]}"))));
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        openAiProvider.complete("You are a parser", "Parse this email");

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForObject(any(String.class), captor.capture(), eq(Map.class));

        HttpEntity<Map<String, Object>> captured = captor.getValue();
        Map<String, Object> body = captured.getBody();
        List<Map<String, String>> messages = (List<Map<String, String>>) body.get("messages");
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).get("role")).isEqualTo("system");
        assertThat(messages.get(0).get("content")).isEqualTo("You are a parser");
        assertThat(messages.get(1).get("role")).isEqualTo("user");
        assertThat(messages.get(1).get("content")).isEqualTo("Parse this email");
    }
}
