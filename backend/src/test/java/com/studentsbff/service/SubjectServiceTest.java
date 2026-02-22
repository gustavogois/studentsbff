package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.studentsbff.dto.SubjectRequest;
import com.studentsbff.dto.SubjectResponse;
import com.studentsbff.mapper.SubjectMapper;
import com.studentsbff.model.Student;
import com.studentsbff.model.Subject;
import com.studentsbff.model.User;
import com.studentsbff.model.Role;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.SubjectRepository;
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
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private SubjectService subjectService;

    private UUID studentId;
    private Student student;
    private Subject subject;
    private SubjectResponse subjectResponse;

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

        UUID subjectId = UUID.randomUUID();
        Instant now = Instant.now();
        subject =
                Subject.builder()
                        .id(subjectId)
                        .name("Mathematics")
                        .student(student)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
        subjectResponse = new SubjectResponse(subjectId, "Mathematics", now, now);
    }

    @Test
    void shouldFindAllByStudentId() {
        when(subjectRepository.findAllByStudentId(studentId)).thenReturn(List.of(subject));
        when(subjectMapper.toResponse(subject)).thenReturn(subjectResponse);

        List<SubjectResponse> result = subjectService.findAllByStudentId(studentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Mathematics");
    }

    @Test
    void shouldFindById() {
        when(subjectRepository.findById(subject.getId())).thenReturn(Optional.of(subject));
        when(subjectMapper.toResponse(subject)).thenReturn(subjectResponse);

        SubjectResponse result = subjectService.findById(subject.getId(), studentId);

        assertThat(result.name()).isEqualTo("Mathematics");
    }

    @Test
    void shouldThrowWhenSubjectNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(subjectRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.findById(unknownId, studentId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldThrowWhenAccessDenied() {
        UUID otherStudentId = UUID.randomUUID();
        when(subjectRepository.findById(subject.getId())).thenReturn(Optional.of(subject));

        assertThatThrownBy(() -> subjectService.findById(subject.getId(), otherStudentId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void shouldCreateSubject() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Science");

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);
        when(subjectMapper.toResponse(subject)).thenReturn(subjectResponse);

        SubjectResponse result = subjectService.create(request, studentId);

        assertThat(result).isNotNull();
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    void shouldDeleteSubject() {
        when(subjectRepository.findById(subject.getId())).thenReturn(Optional.of(subject));

        subjectService.delete(subject.getId(), studentId);

        verify(subjectRepository).delete(subject);
    }
}
