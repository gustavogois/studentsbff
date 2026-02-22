package com.studentsbff.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentsbff.config.OAuth2AuthenticationSuccessHandler;
import com.studentsbff.config.SecurityConfig;
import com.studentsbff.dto.SubjectRequest;
import com.studentsbff.dto.SubjectResponse;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.JwtService;
import com.studentsbff.service.SubjectService;
import java.time.Instant;
import java.util.List;
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

@WebMvcTest(SubjectController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubjectService subjectService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private UUID studentId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
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
    void shouldListSubjects() throws Exception {
        Instant now = Instant.now();
        SubjectResponse response =
                new SubjectResponse(UUID.randomUUID(), "Mathematics", now, now);
        when(subjectService.findAllByStudentId(studentId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mathematics"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldCreateSubject() throws Exception {
        SubjectRequest request = new SubjectRequest();
        request.setName("Science");

        Instant now = Instant.now();
        SubjectResponse response =
                new SubjectResponse(UUID.randomUUID(), "Science", now, now);
        when(subjectService.create(any(SubjectRequest.class), eq(studentId))).thenReturn(response);

        mockMvc.perform(
                        post("/api/subjects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Science"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldGetSubject() throws Exception {
        UUID subjectId = UUID.randomUUID();
        Instant now = Instant.now();
        SubjectResponse response =
                new SubjectResponse(subjectId, "Mathematics", now, now);
        when(subjectService.findById(subjectId, studentId)).thenReturn(response);

        mockMvc.perform(get("/api/subjects/{id}", subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mathematics"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUpdateSubject() throws Exception {
        UUID subjectId = UUID.randomUUID();
        SubjectRequest request = new SubjectRequest();
        request.setName("Updated Math");

        Instant now = Instant.now();
        SubjectResponse response =
                new SubjectResponse(subjectId, "Updated Math", now, now);
        when(subjectService.update(eq(subjectId), any(SubjectRequest.class), eq(studentId)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/subjects/{id}", subjectId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Math"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldDeleteSubject() throws Exception {
        UUID subjectId = UUID.randomUUID();

        mockMvc.perform(delete("/api/subjects/{id}", subjectId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/subjects")).andExpect(status().isUnauthorized());
    }
}
