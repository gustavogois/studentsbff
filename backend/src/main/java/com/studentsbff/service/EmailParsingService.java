package com.studentsbff.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentsbff.ai.LLMProvider;
import com.studentsbff.dto.EmailMessage;
import com.studentsbff.dto.ParsedSchoolEvent;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailParsingService {

    private static final Logger log = LoggerFactory.getLogger(EmailParsingService.class);

    private static final String SYSTEM_PROMPT =
            "You are a school email parser. Extract school events (exams, assignments, deadlines)"
                + " from the provided emails. For each event found, return a JSON object with: title"
                + " (string), eventType (one of: EXAM, ASSIGNMENT, DEADLINE, OTHER), eventDate (ISO"
                + " 8601 format, e.g. 2025-03-15T10:00:00Z), description (string), and"
                + " relatedSubjectName (string or null, match against the student's subjects if"
                + " possible). Return a JSON object with a single key \"events\" containing an array"
                + " of event objects. If no school events are found, return {\"events\": []}.";

    private final LLMProvider llmProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public EmailParsingService(LLMProvider llmProvider) {
        this.llmProvider = llmProvider;
        this.objectMapper = new ObjectMapper();
    }

    EmailParsingService(LLMProvider llmProvider, ObjectMapper objectMapper) {
        this.llmProvider = llmProvider;
        this.objectMapper = objectMapper;
    }

    public List<ParsedSchoolEvent> parseEmails(
            List<EmailMessage> emails, List<String> subjectNames) {
        if (emails.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder userMessage = new StringBuilder();
        userMessage.append("Student's subjects: ").append(String.join(", ", subjectNames));
        userMessage.append("\n\nEmails to parse:\n\n");

        for (EmailMessage email : emails) {
            userMessage.append("--- Email (ID: ").append(email.messageId()).append(") ---\n");
            userMessage.append("From: ").append(email.from()).append("\n");
            userMessage.append("Subject: ").append(email.subject()).append("\n");
            userMessage.append("Date: ").append(email.receivedAt()).append("\n");
            userMessage.append("Body:\n").append(email.body()).append("\n\n");
        }

        try {
            String response = llmProvider.complete(SYSTEM_PROMPT, userMessage.toString());
            return parseResponse(response, emails);
        } catch (Exception e) {
            log.error("Failed to parse emails with LLM", e);
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private List<ParsedSchoolEvent> parseResponse(String response, List<EmailMessage> emails) {
        try {
            Map<String, Object> parsed =
                    objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

            Object eventsObj = parsed.get("events");
            if (eventsObj == null) {
                return Collections.emptyList();
            }

            String eventsJson = objectMapper.writeValueAsString(eventsObj);
            List<Map<String, Object>> eventMaps =
                    objectMapper.readValue(
                            eventsJson, new TypeReference<List<Map<String, Object>>>() {});

            return eventMaps.stream()
                    .map(
                            map -> {
                                String sourceEmailId =
                                        map.containsKey("sourceEmailId")
                                                ? (String) map.get("sourceEmailId")
                                                : (emails.size() == 1
                                                        ? emails.get(0).messageId()
                                                        : null);
                                return new ParsedSchoolEvent(
                                        (String) map.getOrDefault("title", "Untitled"),
                                        mapEventType(
                                                (String) map.getOrDefault("eventType", "OTHER")),
                                        (String) map.getOrDefault("eventDate", ""),
                                        (String) map.getOrDefault("description", ""),
                                        (String) map.get("relatedSubjectName"),
                                        sourceEmailId);
                            })
                    .toList();
        } catch (Exception e) {
            log.error("Failed to parse LLM response: {}", response, e);
            return Collections.emptyList();
        }
    }

    private String mapEventType(String type) {
        return switch (type.toUpperCase()) {
            case "EXAM" -> "EXAM";
            case "ASSIGNMENT" -> "ASSIGNMENT";
            case "DEADLINE" -> "DEADLINE";
            default -> "OTHER";
        };
    }
}
