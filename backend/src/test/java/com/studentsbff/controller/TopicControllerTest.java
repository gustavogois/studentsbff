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
import com.studentsbff.dto.TopicRequest;
import com.studentsbff.dto.TopicResponse;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.JwtService;
import com.studentsbff.service.TopicService;
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

@WebMvcTest(TopicController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TopicService topicService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private UUID studentId;
    private UUID subjectId;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        subjectId = UUID.randomUUID();

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
    void shouldListTopics() throws Exception {
        Instant now = Instant.now();
        TopicResponse response =
                new TopicResponse(UUID.randomUUID(), "Algebra", 3, now, now);
        when(topicService.findAllBySubjectId(subjectId, studentId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/subjects/{subjectId}/topics", subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Algebra"))
                .andExpect(jsonPath("$[0].difficulty").value(3));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldCreateTopic() throws Exception {
        TopicRequest request = new TopicRequest();
        request.setName("Geometry");
        request.setDifficulty(4);

        Instant now = Instant.now();
        TopicResponse response =
                new TopicResponse(UUID.randomUUID(), "Geometry", 4, now, now);
        when(topicService.create(eq(subjectId), any(TopicRequest.class), eq(studentId)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/subjects/{subjectId}/topics", subjectId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Geometry"))
                .andExpect(jsonPath("$.difficulty").value(4));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUpdateTopic() throws Exception {
        UUID topicId = UUID.randomUUID();
        TopicRequest request = new TopicRequest();
        request.setName("Updated Algebra");
        request.setDifficulty(5);

        Instant now = Instant.now();
        TopicResponse response =
                new TopicResponse(topicId, "Updated Algebra", 5, now, now);
        when(topicService.update(eq(subjectId), eq(topicId), any(TopicRequest.class), eq(studentId)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/subjects/{subjectId}/topics/{id}", subjectId, topicId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Algebra"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldDeleteTopic() throws Exception {
        UUID topicId = UUID.randomUUID();

        mockMvc.perform(delete("/api/subjects/{subjectId}/topics/{id}", subjectId, topicId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/subjects/{subjectId}/topics", subjectId))
                .andExpect(status().isUnauthorized());
    }
}
