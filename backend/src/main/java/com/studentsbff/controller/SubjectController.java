package com.studentsbff.controller;

import com.studentsbff.dto.SubjectRequest;
import com.studentsbff.dto.SubjectResponse;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.SubjectService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public SubjectController(
            SubjectService subjectService,
            UserRepository userRepository,
            StudentRepository studentRepository) {
        this.subjectService = subjectService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> listSubjects() {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(subjectService.findAllByStudentId(studentId));
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(
            @Valid @RequestBody SubjectRequest request) {
        UUID studentId = getCurrentStudentId();
        SubjectResponse response = subjectService.create(request, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubject(@PathVariable UUID id) {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(subjectService.findById(id, studentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable UUID id, @Valid @RequestBody SubjectRequest request) {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(subjectService.update(id, request, studentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable UUID id) {
        UUID studentId = getCurrentStudentId();
        subjectService.delete(id, studentId);
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
