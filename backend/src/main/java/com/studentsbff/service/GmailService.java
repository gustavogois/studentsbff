package com.studentsbff.service;

import com.studentsbff.dto.EmailMessage;
import com.studentsbff.model.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GmailService {

    private static final String GMAIL_API_BASE = "https://gmail.googleapis.com/gmail/v1/users/me";

    private final RestTemplate restTemplate;
    private final int maxMessages;
    private final OAuth2TokenService oAuth2TokenService;

    public GmailService(
            OAuth2TokenService oAuth2TokenService,
            @Value("${gmail.max-messages:50}") int maxMessages) {
        this.restTemplate = new RestTemplate();
        this.oAuth2TokenService = oAuth2TokenService;
        this.maxMessages = maxMessages;
    }

    public List<EmailMessage> fetchRecentEmails(User user, int daysBack) {
        if (user.getGoogleAccessToken() == null) {
            throw new IllegalStateException(
                    "No Gmail access token found. Please re-login with Gmail permissions.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getGoogleAccessToken());

        String query = "newer_than:" + daysBack + "d";
        String url =
                GMAIL_API_BASE
                        + "/messages?q="
                        + query
                        + "&maxResults="
                        + maxMessages;

        ResponseEntity<Map> listResponse =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        Map<String, Object> listBody = listResponse.getBody();
        if (listBody == null || !listBody.containsKey("messages")) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = (List<Map<String, String>>) listBody.get("messages");

        List<EmailMessage> result = new ArrayList<>();
        for (Map<String, String> msg : messages) {
            String messageId = msg.get("id");
            EmailMessage email = fetchMessageDetail(messageId, headers);
            if (email != null) {
                result.add(email);
            }
        }
        return result;
    }

    private EmailMessage fetchMessageDetail(String messageId, HttpHeaders headers) {
        String url = GMAIL_API_BASE + "/messages/" + messageId + "?format=full";

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) {
            return null;
        }

        String id = (String) body.get("id");
        String snippet = (String) body.getOrDefault("snippet", "");
        long internalDate = Long.parseLong(String.valueOf(body.getOrDefault("internalDate", "0")));
        Instant receivedAt = Instant.ofEpochMilli(internalDate);

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) body.get("payload");

        String from = "";
        String subject = "";
        String emailBody = snippet;

        if (payload != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> payloadHeaders =
                    (List<Map<String, String>>) payload.getOrDefault("headers", Collections.emptyList());

            for (Map<String, String> header : payloadHeaders) {
                String name = header.getOrDefault("name", "");
                if ("From".equalsIgnoreCase(name)) {
                    from = header.getOrDefault("value", "");
                } else if ("Subject".equalsIgnoreCase(name)) {
                    subject = header.getOrDefault("value", "");
                }
            }

            emailBody = extractPlainTextBody(payload, snippet);
        }

        return new EmailMessage(id, from, subject, emailBody, receivedAt);
    }

    @SuppressWarnings("unchecked")
    private String extractPlainTextBody(Map<String, Object> payload, String fallback) {
        String mimeType = (String) payload.getOrDefault("mimeType", "");

        if ("text/plain".equals(mimeType)) {
            Map<String, String> bodyObj = (Map<String, String>) payload.get("body");
            if (bodyObj != null && bodyObj.containsKey("data")) {
                return decodeBase64Url(bodyObj.get("data"));
            }
        }

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) payload.getOrDefault("parts", Collections.emptyList());
        for (Map<String, Object> part : parts) {
            String partMime = (String) part.getOrDefault("mimeType", "");
            if ("text/plain".equals(partMime)) {
                Map<String, String> bodyObj = (Map<String, String>) part.get("body");
                if (bodyObj != null && bodyObj.containsKey("data")) {
                    return decodeBase64Url(bodyObj.get("data"));
                }
            }
        }

        return fallback;
    }

    private String decodeBase64Url(String encoded) {
        byte[] decoded = Base64.getUrlDecoder().decode(encoded);
        return new String(decoded);
    }
}
