package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.studentsbff.dto.SchoolEventRequest;
import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.mapper.SchoolEventMapper;
import com.studentsbff.model.EventType;
import com.studentsbff.model.Role;
import com.studentsbff.model.SchoolEvent;
import com.studentsbff.model.Student;
import com.studentsbff.model.Subject;
import com.studentsbff.model.User;
import com.studentsbff.repository.SchoolEventRepository;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
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

    @Mock private SchoolEventRepository schoolEventRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private SchoolEventMapper schoolEventMapper;

    @InjectMocks private SchoolEventService schoolEventService;

    private UUID studentId;
    private UUID eventId;
    private Student student;
    private SchoolEvent event;
    private SchoolEventResponse eventResponse;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        User user =
                User.builder()
                        .id(UUID.randomUUID())
                        .name("Test User")
                        .email("test@example.com")
                        .role(Role.STUDENT)
                        .build();

        student = Student.builder().id(studentId).user(user).build();

        Instant now = Instant.now();
        event =
                SchoolEvent.builder()
                        .id(eventId)
                        .student(student)
                        .title("Math Exam")
                        .eventType(EventType.EXAM)
                        .eventDate(LocalDate.of(2026, 3, 15))
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

        eventResponse =
                new SchoolEventResponse(
                        eventId,
                        "Math Exam",
                        EventType.EXAM,
                        LocalDate.of(2026, 3, 15),
                        null,
                        null,
                        null,
                        now,
                        now);
    }

    @Test
    void shouldCreateEvent() {
        SchoolEventRequest request = new SchoolEventRequest();
        request.setTitle("Math Exam");
        request.setEventType(EventType.EXAM);
        request.setEventDate(LocalDate.of(2026, 3, 15));

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(schoolEventRepository.save(any(SchoolEvent.class))).thenReturn(event);
        when(schoolEventMapper.toResponse(event)).thenReturn(eventResponse);

        SchoolEventResponse result = schoolEventService.create(request, studentId);

        assertThat(result.title()).isEqualTo("Math Exam");
        assertThat(result.eventType()).isEqualTo(EventType.EXAM);
        verify(schoolEventRepository).save(any(SchoolEvent.class));
    }

    @Test
    void shouldCreateEventWithSubjectLink() {
        UUID subjectId = UUID.randomUUID();
        Subject subject =
                Subject.builder().id(subjectId).name("Mathematics").student(student).build();

        SchoolEventRequest request = new SchoolEventRequest();
        request.setTitle("Math Exam");
        request.setEventType(EventType.EXAM);
        request.setEventDate(LocalDate.of(2026, 3, 15));
        request.setSubjectId(subjectId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(schoolEventRepository.save(any(SchoolEvent.class))).thenReturn(event);
        when(schoolEventMapper.toResponse(event)).thenReturn(eventResponse);

        SchoolEventResponse result = schoolEventService.create(request, studentId);

        assertThat(result).isNotNull();
        verify(subjectRepository).findById(subjectId);
    }

    @Test
    void shouldListEventsByStudent() {
        when(schoolEventRepository.findAllByStudentId(studentId))
                .thenReturn(List.of(event));
        when(schoolEventMapper.toResponse(event)).thenReturn(eventResponse);

        List<SchoolEventResponse> results =
                schoolEventService.findAll(studentId, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("Math Exam");
    }

    @Test
    void shouldListEventsByDateRange() {
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 3, 31);

        when(schoolEventRepository.findAllByStudentIdAndEventDateBetween(
                        studentId, from, to))
                .thenReturn(List.of(event));
        when(schoolEventMapper.toResponse(event)).thenReturn(eventResponse);

        List<SchoolEventResponse> results =
                schoolEventService.findAll(studentId, from, to);

        assertThat(results).hasSize(1);
        verify(schoolEventRepository)
                .findAllByStudentIdAndEventDateBetween(studentId, from, to);
    }

    @Test
    void shouldUpdateEvent() {
        SchoolEventRequest request = new SchoolEventRequest();
        request.setTitle("Updated Exam");
        request.setEventType(EventType.ASSIGNMENT);
        request.setEventDate(LocalDate.of(2026, 4, 1));

        when(schoolEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(schoolEventRepository.save(event)).thenReturn(event);
        when(schoolEventMapper.toResponse(event)).thenReturn(eventResponse);

        SchoolEventResponse result =
                schoolEventService.update(eventId, request, studentId);

        assertThat(result).isNotNull();
        verify(schoolEventRepository).save(event);
    }

    @Test
    void shouldDeleteEvent() {
        when(schoolEventRepository.findById(eventId)).thenReturn(Optional.of(event));

        schoolEventService.delete(eventId, studentId);

        verify(schoolEventRepository).delete(event);
    }

    @Test
    void shouldThrowWhenEventNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(schoolEventRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schoolEventService.findById(unknownId, studentId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldThrowWhenEventNotOwnedByStudent() {
        UUID otherStudentId = UUID.randomUUID();
        when(schoolEventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> schoolEventService.findById(eventId, otherStudentId))
                .isInstanceOf(AccessDeniedException.class);
    }
}
