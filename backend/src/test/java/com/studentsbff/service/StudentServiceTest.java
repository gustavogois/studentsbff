package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.studentsbff.dto.StudentProfileRequest;
import com.studentsbff.dto.StudentProfileResponse;
import com.studentsbff.mapper.StudentMapper;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private UUID studentId;
    private Student student;
    private StudentProfileResponse profileResponse;

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
        Instant now = Instant.now();
        student =
                Student.builder()
                        .id(studentId)
                        .user(user)
                        .grade("7th")
                        .school("Lincoln Middle School")
                        .createdAt(now)
                        .build();
        profileResponse = new StudentProfileResponse(studentId, "7th", "Lincoln Middle School", now);
    }

    @Test
    void shouldGetStudentProfile() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentMapper.toProfileResponse(student)).thenReturn(profileResponse);

        StudentProfileResponse result = studentService.getProfile(studentId);

        assertThat(result.grade()).isEqualTo("7th");
        assertThat(result.school()).isEqualTo("Lincoln Middle School");
    }

    @Test
    void shouldUpdateStudentProfile() {
        StudentProfileRequest request = new StudentProfileRequest();
        request.setGrade("8th");
        request.setSchool("Washington Middle School");

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toProfileResponse(student)).thenReturn(profileResponse);

        StudentProfileResponse result = studentService.updateProfile(studentId, request);

        assertThat(result).isNotNull();
        verify(studentRepository).save(student);
    }

    @Test
    void shouldThrowWhenStudentNotFoundOnGetProfile() {
        UUID unknownId = UUID.randomUUID();
        when(studentRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getProfile(unknownId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
