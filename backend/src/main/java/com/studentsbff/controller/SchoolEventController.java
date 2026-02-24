package com.studentsbff.controller;

import com.studentsbff.dto.SchoolEventRequest;
import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.SchoolEventService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<List<SchoolEventResponse>> listEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate to) {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(schoolEventService.findAll(studentId, from, to));
    }

    @PostMapping
    public ResponseEntity<SchoolEventResponse> createEvent(
            @Valid @RequestBody SchoolEventRequest request) {
        UUID studentId = getCurrentStudentId();
        SchoolEventResponse response = schoolEventService.create(request, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolEventResponse> getEvent(@PathVariable UUID id) {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(schoolEventService.findById(id, studentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolEventResponse> updateEvent(
            @PathVariable UUID id, @Valid @RequestBody SchoolEventRequest request) {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(
                schoolEventService.update(id, request, studentId));
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
                        .orElseThrow(
                                () -> new EntityNotFoundException("User not found"));
        Student student =
                studentRepository
                        .findByUserId(user.getId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Student profile not found"));
        return student.getId();
    }
}
