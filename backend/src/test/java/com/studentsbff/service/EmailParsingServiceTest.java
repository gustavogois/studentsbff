package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentsbff.ai.LLMProvider;
import com.studentsbff.dto.EmailMessage;
import com.studentsbff.dto.ParsedSchoolEvent;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailParsingServiceTest {

    @Mock
    private LLMProvider llmProvider;

    private EmailParsingService emailParsingService;

    @BeforeEach
    void setUp() {
        emailParsingService = new EmailParsingService(llmProvider, new ObjectMapper());
    }

    private EmailMessage sampleEmail() {
        return new EmailMessage(
                "msg-001",
                "teacher@school.edu",
                "Math Exam next Friday",
                "Dear students, there will be a math exam next Friday covering chapters 5-7.",
                Instant.parse("2025-03-10T10:00:00Z"));
    }

    @Test
    void shouldExtractExamEvent() {
        String llmResponse =
                "{\"events\":[{\"title\":\"Math Exam\","
                        + "\"eventType\":\"EXAM\","
                        + "\"eventDate\":\"2025-03-14T10:00:00Z\","
                        + "\"description\":\"Exam covering chapters 5-7\","
                        + "\"relatedSubjectName\":\"Math\"}]}";
        when(llmProvider.complete(any(), any())).thenReturn(llmResponse);

        List<ParsedSchoolEvent> result =
                emailParsingService.parseEmails(List.of(sampleEmail()), List.of("Math", "Science"));

        assertThat(result).hasSize(1);
        ParsedSchoolEvent event = result.get(0);
        assertThat(event.title()).isEqualTo("Math Exam");
        assertThat(event.eventType()).isEqualTo("EXAM");
        assertThat(event.eventDate()).isEqualTo("2025-03-14T10:00:00Z");
        assertThat(event.relatedSubjectName()).isEqualTo("Math");
    }

    @Test
    void shouldExtractMultipleEvents() {
        String llmResponse =
                "{\"events\":["
                        + "{\"title\":\"Math Exam\",\"eventType\":\"EXAM\",\"eventDate\":\"2025-03-14T10:00:00Z\",\"description\":\"Exam\",\"relatedSubjectName\":\"Math\"},"
                        + "{\"title\":\"Science Report Due\",\"eventType\":\"ASSIGNMENT\",\"eventDate\":\"2025-03-15T23:59:00Z\",\"description\":\"Report\",\"relatedSubjectName\":\"Science\"}"
                        + "]}";
        when(llmProvider.complete(any(), any())).thenReturn(llmResponse);

        List<ParsedSchoolEvent> result =
                emailParsingService.parseEmails(List.of(sampleEmail()), List.of("Math", "Science"));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).eventType()).isEqualTo("EXAM");
        assertThat(result.get(1).eventType()).isEqualTo("ASSIGNMENT");
    }

    @Test
    void shouldHandleNoEventsFound() {
        String llmResponse = "{\"events\":[]}";
        when(llmProvider.complete(any(), any())).thenReturn(llmResponse);

        List<ParsedSchoolEvent> result =
                emailParsingService.parseEmails(List.of(sampleEmail()), List.of("Math"));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapEventTypesCorrectly() {
        String llmResponse =
                "{\"events\":["
                        + "{\"title\":\"E1\",\"eventType\":\"EXAM\",\"eventDate\":\"2025-03-14T10:00:00Z\",\"description\":\"\",\"relatedSubjectName\":null},"
                        + "{\"title\":\"E2\",\"eventType\":\"ASSIGNMENT\",\"eventDate\":\"2025-03-14T10:00:00Z\",\"description\":\"\",\"relatedSubjectName\":null},"
                        + "{\"title\":\"E3\",\"eventType\":\"DEADLINE\",\"eventDate\":\"2025-03-14T10:00:00Z\",\"description\":\"\",\"relatedSubjectName\":null},"
                        + "{\"title\":\"E4\",\"eventType\":\"OTHER\",\"eventDate\":\"2025-03-14T10:00:00Z\",\"description\":\"\",\"relatedSubjectName\":null}"
                        + "]}";
        when(llmProvider.complete(any(), any())).thenReturn(llmResponse);

        List<ParsedSchoolEvent> result =
                emailParsingService.parseEmails(List.of(sampleEmail()), List.of("Math"));

        assertThat(result).hasSize(4);
        assertThat(result.get(0).eventType()).isEqualTo("EXAM");
        assertThat(result.get(1).eventType()).isEqualTo("ASSIGNMENT");
        assertThat(result.get(2).eventType()).isEqualTo("DEADLINE");
        assertThat(result.get(3).eventType()).isEqualTo("OTHER");
    }

    @Test
    void shouldIncludeStudentSubjectsInPrompt() {
        when(llmProvider.complete(any(), any())).thenReturn("{\"events\":[]}");

        emailParsingService.parseEmails(
                List.of(sampleEmail()), List.of("Math", "Science", "English"));

        ArgumentCaptor<String> userMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(llmProvider).complete(any(), userMessageCaptor.capture());

        String userMessage = userMessageCaptor.getValue();
        assertThat(userMessage).contains("Math");
        assertThat(userMessage).contains("Science");
        assertThat(userMessage).contains("English");
    }

    @Test
    void shouldHandleMalformedLLMResponse() {
        when(llmProvider.complete(any(), any())).thenReturn("this is not valid JSON");

        List<ParsedSchoolEvent> result =
                emailParsingService.parseEmails(List.of(sampleEmail()), List.of("Math"));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyForEmptyEmailList() {
        List<ParsedSchoolEvent> result =
                emailParsingService.parseEmails(Collections.emptyList(), List.of("Math"));

        assertThat(result).isEmpty();
    }
}
