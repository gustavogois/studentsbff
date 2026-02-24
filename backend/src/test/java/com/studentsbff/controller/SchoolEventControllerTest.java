package com.studentsbff.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
import com.studentsbff.dto.SchoolEventRequest;
import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.model.EventType;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.JwtService;
import com.studentsbff.service.SchoolEventService;
import java.time.Instant;
import java.time.LocalDate;
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

@WebMvcTest(SchoolEventController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class SchoolEventControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private SchoolEventService schoolEventService;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private StudentRepository studentRepository;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

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
    void shouldCreateEvent() throws Exception {
        SchoolEventRequest request = new SchoolEventRequest();
        request.setTitle("Math Exam");
        request.setEventType(EventType.EXAM);
        request.setEventDate(LocalDate.of(2026, 3, 15));

        Instant now = Instant.now();
        UUID eventId = UUID.randomUUID();
        SchoolEventResponse response =
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

        when(schoolEventService.create(any(SchoolEventRequest.class), eq(studentId)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/school-events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Math Exam"))
                .andExpect(jsonPath("$.eventType").value("EXAM"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldListEvents() throws Exception {
        Instant now = Instant.now();
        SchoolEventResponse response =
                new SchoolEventResponse(
                        UUID.randomUUID(),
                        "Math Exam",
                        EventType.EXAM,
                        LocalDate.of(2026, 3, 15),
                        null,
                        null,
                        null,
                        now,
                        now);

        when(schoolEventService.findAll(eq(studentId), any(), any()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/school-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Math Exam"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldGetEventById() throws Exception {
        UUID eventId = UUID.randomUUID();
        Instant now = Instant.now();
        SchoolEventResponse response =
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

        when(schoolEventService.findById(eventId, studentId)).thenReturn(response);

        mockMvc.perform(get("/api/school-events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Math Exam"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUpdateEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        SchoolEventRequest request = new SchoolEventRequest();
        request.setTitle("Updated Exam");
        request.setEventType(EventType.ASSIGNMENT);
        request.setEventDate(LocalDate.of(2026, 4, 1));

        Instant now = Instant.now();
        SchoolEventResponse response =
                new SchoolEventResponse(
                        eventId,
                        "Updated Exam",
                        EventType.ASSIGNMENT,
                        LocalDate.of(2026, 4, 1),
                        null,
                        null,
                        null,
                        now,
                        now);

        when(schoolEventService.update(
                        eq(eventId), any(SchoolEventRequest.class), eq(studentId)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/school-events/{id}", eventId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Exam"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldDeleteEvent() throws Exception {
        UUID eventId = UUID.randomUUID();

        mockMvc.perform(delete("/api/school-events/{id}", eventId))
                .andExpect(status().isNoContent());

        verify(schoolEventService).delete(eventId, studentId);
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/school-events")).andExpect(status().isUnauthorized());
    }
}
