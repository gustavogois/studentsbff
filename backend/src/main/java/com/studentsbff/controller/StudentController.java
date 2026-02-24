package com.studentsbff.controller;

import com.studentsbff.dto.GradeResponse;
import com.studentsbff.dto.StudentProfileRequest;
import com.studentsbff.dto.StudentProfileResponse;
import com.studentsbff.model.Grade;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public StudentController(
            StudentService studentService,
            UserRepository userRepository,
            StudentRepository studentRepository) {
        this.studentService = studentService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/grades")
    public ResponseEntity<List<GradeResponse>> listGrades() {
        List<GradeResponse> grades =
                Arrays.stream(Grade.values())
                        .map(g -> new GradeResponse(g.name(), g.getLabel()))
                        .toList();
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentProfileResponse> getProfile() {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(studentService.getProfile(studentId));
    }

    @PutMapping("/profile")
    public ResponseEntity<StudentProfileResponse> updateProfile(
            @RequestBody StudentProfileRequest request) {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(studentService.updateProfile(studentId, request));
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
