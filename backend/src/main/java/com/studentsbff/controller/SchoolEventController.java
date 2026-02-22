package com.studentsbff.controller;

import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.SchoolEventService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/school-events")
public class SchoolEventController {

    private final SchoolEventService schoolEventService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public SchoolEventController(
            SchoolEventService schoolEventService,
            UserRepository userRepository,
            StudentRepository studentRepository) {
        this.schoolEventService = schoolEventService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public ResponseEntity<List<SchoolEventResponse>> listEvents() {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(schoolEventService.findAllByStudentId(studentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        UUID studentId = getCurrentStudentId();
        schoolEventService.delete(id, studentId);
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentStudentId() {
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
        return student.getId();
    }
}
