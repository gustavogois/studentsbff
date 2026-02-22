package com.studentsbff.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.studentsbff.config.OAuth2AuthenticationSuccessHandler;
import com.studentsbff.config.SecurityConfig;
import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.JwtService;
import com.studentsbff.service.SchoolEventService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SchoolEventController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class SchoolEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SchoolEventService schoolEventService;

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
    void shouldListEvents() throws Exception {
        Instant now = Instant.now();
        SchoolEventResponse event =
                new SchoolEventResponse(
                        UUID.randomUUID(),
                        "Math Exam",
                        "EXAM",
                        null,
                        null,
                        "Exam on chapters 5-7",
                        now,
                        "GMAIL",
                        now);
        when(schoolEventService.findAllByStudentId(studentId)).thenReturn(List.of(event));

        mockMvc.perform(get("/api/school-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Math Exam"))
                .andExpect(jsonPath("$[0].eventType").value("EXAM"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldDeleteEvent() throws Exception {
        UUID eventId = UUID.randomUUID();

        mockMvc.perform(delete("/api/school-events/" + eventId))
                .andExpect(status().isNoContent());

        verify(schoolEventService).delete(eventId, studentId);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturn404WhenEventNotFound() throws Exception {
        UUID eventId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("School event not found with id: " + eventId))
                .when(schoolEventService)
                .delete(eventId, studentId);

        mockMvc.perform(delete("/api/school-events/" + eventId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/school-events")).andExpect(status().isUnauthorized());
    }
}
