package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.studentsbff.dto.ParsedSchoolEvent;
import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.mapper.SchoolEventMapper;
import com.studentsbff.model.EventSource;
import com.studentsbff.model.EventType;
import com.studentsbff.model.Role;
import com.studentsbff.model.SchoolEvent;
import com.studentsbff.model.Student;
import com.studentsbff.model.Subject;
import com.studentsbff.model.User;
import com.studentsbff.repository.SchoolEventRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class SchoolEventServiceTest {

    @Mock
    private SchoolEventRepository schoolEventRepository;

    @Mock
    private SchoolEventMapper schoolEventMapper;

    @InjectMocks
    private SchoolEventService schoolEventService;

    private UUID studentId;
    private Student student;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        User user =
                User.builder()
                        .id(UUID.randomUUID())
                        .name("Test User")
                        .email("test@example.com")
                        .role(Role.STUDENT)
                        .build();
        student = Student.builder().id(studentId).user(user).build();
    }

    @Test
    void shouldListEventsForStudent() {
        Instant now = Instant.now();
        SchoolEvent event1 =
                SchoolEvent.builder()
                        .id(UUID.randomUUID())
                        .student(student)
                        .title("Math Exam")
                        .eventType(EventType.EXAM)
                        .eventDate(now)
                        .source(EventSource.GMAIL)
                        .build();
        SchoolEvent event2 =
                SchoolEvent.builder()
                        .id(UUID.randomUUID())
                        .student(student)
                        .title("Science Report")
                        .eventType(EventType.ASSIGNMENT)
                        .eventDate(now)
                        .source(EventSource.GMAIL)
                        .build();

        when(schoolEventRepository.findAllByStudentIdOrderByEventDateAsc(studentId))
                .thenReturn(List.of(event1, event2));

        SchoolEventResponse resp1 =
                new SchoolEventResponse(
                        event1.getId(), "Math Exam", "EXAM", null, null, null, now, "GMAIL", now);
        SchoolEventResponse resp2 =
                new SchoolEventResponse(
                        event2.getId(),
                        "Science Report",
                        "ASSIGNMENT",
                        null,
                        null,
                        null,
                        now,
                        "GMAIL",
                        now);
        when(schoolEventMapper.toResponse(event1)).thenReturn(resp1);
        when(schoolEventMapper.toResponse(event2)).thenReturn(resp2);

        List<SchoolEventResponse> result = schoolEventService.findAllByStudentId(studentId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Math Exam");
        assertThat(result.get(1).title()).isEqualTo("Science Report");
    }

    @Test
    void shouldDeleteEvent() {
        UUID eventId = UUID.randomUUID();
        SchoolEvent event =
                SchoolEvent.builder().id(eventId).student(student).title("Test").build();

        when(schoolEventRepository.findById(eventId)).thenReturn(Optional.of(event));

        schoolEventService.delete(eventId, studentId);

        verify(schoolEventRepository).delete(event);
    }

    @Test
    void shouldThrowWhenNotOwner() {
        UUID eventId = UUID.randomUUID();
        UUID otherStudentId = UUID.randomUUID();
        Student otherStudent =
                Student.builder()
                        .id(otherStudentId)
                        .user(
                                User.builder()
                                        .id(UUID.randomUUID())
                                        .email("other@example.com")
                                        .name("Other")
                                        .role(Role.STUDENT)
                                        .build())
                        .build();
        SchoolEvent event =
                SchoolEvent.builder().id(eventId).student(otherStudent).title("Test").build();

        when(schoolEventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> schoolEventService.delete(eventId, studentId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void shouldThrowWhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        when(schoolEventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schoolEventService.delete(eventId, studentId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldSaveEventsFromParsedData() {
        Subject mathSubject =
                Subject.builder().id(UUID.randomUUID()).name("Math").student(student).build();
        ParsedSchoolEvent parsed =
                new ParsedSchoolEvent(
                        "Math Exam",
                        "EXAM",
                        "2025-03-14T10:00:00Z",
                        "Exam covering chapters 5-7",
                        "Math",
                        "msg-001");

        when(schoolEventRepository.save(any(SchoolEvent.class)))
                .thenAnswer(
                        invocation -> {
                            SchoolEvent saved = invocation.getArgument(0);
                            saved.setId(UUID.randomUUID());
                            return saved;
                        });

        Instant now = Instant.now();
        when(schoolEventMapper.toResponse(any(SchoolEvent.class)))
                .thenReturn(
                        new SchoolEventResponse(
                                UUID.randomUUID(),
                                "Math Exam",
                                "EXAM",
                                mathSubject.getId(),
                                "Math",
                                "Exam covering chapters 5-7",
                                now,
                                "GMAIL",
                                now));

        List<SchoolEventResponse> result =
                schoolEventService.saveFromParsed(
                        List.of(parsed), student, List.of(mathSubject));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Math Exam");
        verify(schoolEventRepository).save(any(SchoolEvent.class));
    }
}
