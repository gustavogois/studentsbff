package com.studentsbff.controller;

import com.studentsbff.dto.GmailSyncRequest;
import com.studentsbff.dto.GmailSyncResponse;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.GmailSyncService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gmail")
public class GmailSyncController {

    private final GmailSyncService gmailSyncService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public GmailSyncController(
            GmailSyncService gmailSyncService,
            UserRepository userRepository,
            StudentRepository studentRepository) {
        this.gmailSyncService = gmailSyncService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/sync")
    public ResponseEntity<GmailSyncResponse> syncGmail(@RequestBody GmailSyncRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Student student =
                studentRepository
                        .findByUserId(user.getId())
                        .orElseThrow(
                                () -> new EntityNotFoundException("Student profile not found"));

        int daysBack = request.getDaysBack() != null ? request.getDaysBack() : 30;
        GmailSyncResponse response = gmailSyncService.sync(user, student, daysBack);
        return ResponseEntity.ok(response);
    }
}
