package com.studentsbff.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentsbff.config.OAuth2AuthenticationSuccessHandler;
import com.studentsbff.config.SecurityConfig;
import com.studentsbff.dto.GmailSyncResponse;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.GmailSyncService;
import com.studentsbff.service.JwtService;
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

@WebMvcTest(GmailSyncController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class GmailSyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GmailSyncService gmailSyncService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private User user;
    private Student student;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        user =
                User.builder()
                        .id(userId)
                        .name("Test User")
                        .email("test@example.com")
                        .role(Role.STUDENT)
                        .build();

        student = Student.builder().id(studentId).user(user).build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(studentRepository.findByUserId(userId)).thenReturn(Optional.of(student));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldTriggerSync() throws Exception {
        GmailSyncResponse syncResponse = new GmailSyncResponse(3, 1, 10);
        when(gmailSyncService.sync(any(User.class), any(Student.class), eq(7)))
                .thenReturn(syncResponse);

        mockMvc.perform(
                        post("/api/gmail/sync")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"daysBack\":7}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newEventsCount").value(3))
                .andExpect(jsonPath("$.skippedDuplicates").value(1))
                .andExpect(jsonPath("$.totalEmails").value(10));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(
                        post("/api/gmail/sync")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"daysBack\":7}"))
                .andExpect(status().isUnauthorized());
    }
}
