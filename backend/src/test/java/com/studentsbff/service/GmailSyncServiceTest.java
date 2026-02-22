package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.studentsbff.dto.EmailMessage;
import com.studentsbff.dto.GmailSyncResponse;
import com.studentsbff.dto.ParsedSchoolEvent;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.Subject;
import com.studentsbff.model.User;
import com.studentsbff.repository.SchoolEventRepository;
import com.studentsbff.repository.SubjectRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GmailSyncServiceTest {

    @Mock
    private GmailService gmailService;

    @Mock
    private EmailParsingService emailParsingService;

    @Mock
    private SchoolEventService schoolEventService;

    @Mock
    private SchoolEventRepository schoolEventRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private GmailSyncService gmailSyncService;

    private User user;
    private Student student;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        user =
                User.builder()
                        .id(userId)
                        .name("Test User")
                        .email("test@example.com")
                        .role(Role.STUDENT)
                        .googleAccessToken("valid-token")
                        .googleTokenExpiry(Instant.now().plusSeconds(3600))
                        .build();
        student =
                Student.builder()
                        .id(UUID.randomUUID())
                        .user(user)
                        .build();
    }

    @Test
    void shouldOrchestrateFullSync() {
        EmailMessage email =
                new EmailMessage(
                        "msg-001",
                        "teacher@school.edu",
                        "Math Exam",
                        "Exam body",
                        Instant.now());
        when(gmailService.fetchRecentEmails(user, 7)).thenReturn(List.of(email));
        when(schoolEventRepository.existsBySourceEmailId("msg-001")).thenReturn(false);

        Subject math =
                Subject.builder()
                        .id(UUID.randomUUID())
                        .name("Math")
                        .student(student)
                        .build();
        when(subjectRepository.findAllByStudentId(student.getId())).thenReturn(List.of(math));

        ParsedSchoolEvent parsed =
                new ParsedSchoolEvent(
                        "Math Exam", "EXAM", "2025-03-14T10:00:00Z", "Exam", "Math", "msg-001");
        when(emailParsingService.parseEmails(any(), any())).thenReturn(List.of(parsed));
        when(schoolEventService.saveFromParsed(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        GmailSyncResponse result = gmailSyncService.sync(user, student, 7);

        assertThat(result.newEventsCount()).isEqualTo(1);
        assertThat(result.totalEmails()).isEqualTo(1);
        assertThat(result.skippedDuplicates()).isEqualTo(0);

        verify(gmailService).fetchRecentEmails(user, 7);
        verify(emailParsingService).parseEmails(any(), eq(List.of("Math")));
        verify(schoolEventService).saveFromParsed(any(), eq(student), eq(List.of(math)));
    }

    @Test
    void shouldSkipDuplicateEmails() {
        EmailMessage email =
                new EmailMessage(
                        "msg-001",
                        "teacher@school.edu",
                        "Math Exam",
                        "Exam body",
                        Instant.now());
        when(gmailService.fetchRecentEmails(user, 7)).thenReturn(List.of(email));
        when(schoolEventRepository.existsBySourceEmailId("msg-001")).thenReturn(true);

        GmailSyncResponse result = gmailSyncService.sync(user, student, 7);

        assertThat(result.newEventsCount()).isEqualTo(0);
        assertThat(result.skippedDuplicates()).isEqualTo(1);
        assertThat(result.totalEmails()).isEqualTo(1);
    }

    @Test
    void shouldReturnSyncStats() {
        EmailMessage email1 =
                new EmailMessage("msg-001", "a@b.com", "S1", "B1", Instant.now());
        EmailMessage email2 =
                new EmailMessage("msg-002", "a@b.com", "S2", "B2", Instant.now());
        EmailMessage email3 =
                new EmailMessage("msg-003", "a@b.com", "S3", "B3", Instant.now());

        when(gmailService.fetchRecentEmails(user, 30))
                .thenReturn(List.of(email1, email2, email3));
        when(schoolEventRepository.existsBySourceEmailId("msg-001")).thenReturn(true);
        when(schoolEventRepository.existsBySourceEmailId("msg-002")).thenReturn(false);
        when(schoolEventRepository.existsBySourceEmailId("msg-003")).thenReturn(false);
        when(subjectRepository.findAllByStudentId(student.getId()))
                .thenReturn(Collections.emptyList());

        ParsedSchoolEvent parsed =
                new ParsedSchoolEvent("Event", "OTHER", "2025-03-14T10:00:00Z", "", null, null);
        when(emailParsingService.parseEmails(any(), any())).thenReturn(List.of(parsed));
        when(schoolEventService.saveFromParsed(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        GmailSyncResponse result = gmailSyncService.sync(user, student, 30);

        assertThat(result.totalEmails()).isEqualTo(3);
        assertThat(result.skippedDuplicates()).isEqualTo(1);
        assertThat(result.newEventsCount()).isEqualTo(1);
    }
}
