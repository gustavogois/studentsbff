package com.studentsbff.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentsbff.config.OAuth2AuthenticationSuccessHandler;
import com.studentsbff.config.SecurityConfig;
import com.studentsbff.dto.StudentProfileRequest;
import com.studentsbff.dto.StudentProfileResponse;
import com.studentsbff.model.Grade;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.JwtService;
import com.studentsbff.service.StudentService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudentController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private UUID studentId;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        User user =
                User.builder()
                        .id(userId)
                        .name("Test User")
                        .email("test@example.com")
                        .role(Role.STUDENT)
                        .build();

        Student student = Student.builder().id(studentId).user(user).build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(studentRepository.findByUserId(userId)).thenReturn(Optional.of(student));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldGetProfile() throws Exception {
        Instant now = Instant.now();
        StudentProfileResponse response =
                new StudentProfileResponse(
                        studentId, Grade.GRADE_7, "Lincoln Middle School", "A", now);
        when(studentService.getProfile(studentId)).thenReturn(response);

        mockMvc.perform(get("/api/students/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value("GRADE_7"))
                .andExpect(jsonPath("$.school").value("Lincoln Middle School"))
                .andExpect(jsonPath("$.turma").value("A"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUpdateProfile() throws Exception {
        StudentProfileRequest request = new StudentProfileRequest();
        request.setGrade(Grade.GRADE_8);
        request.setSchool("Washington Middle School");
        request.setTurma("B");

        Instant now = Instant.now();
        StudentProfileResponse response =
                new StudentProfileResponse(
                        studentId, Grade.GRADE_8, "Washington Middle School", "B", now);
        when(studentService.updateProfile(eq(studentId), any(StudentProfileRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/students/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value("GRADE_8"))
                .andExpect(jsonPath("$.school").value("Washington Middle School"))
                .andExpect(jsonPath("$.turma").value("B"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/students/profile")).andExpect(status().isUnauthorized());
    }
}
