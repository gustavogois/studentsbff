package com.studentsbff.controller;

import com.studentsbff.dto.TopicRequest;
import com.studentsbff.dto.TopicResponse;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import com.studentsbff.service.TopicService;
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
@RequestMapping("/api/subjects/{subjectId}/topics")
public class TopicController {

    private final TopicService topicService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public TopicController(
            TopicService topicService,
            UserRepository userRepository,
            StudentRepository studentRepository) {
        this.topicService = topicService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public ResponseEntity<List<TopicResponse>> listTopics(@PathVariable UUID subjectId) {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(topicService.findAllBySubjectId(subjectId, studentId));
    }

    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(
            @PathVariable UUID subjectId, @Valid @RequestBody TopicRequest request) {
        UUID studentId = getCurrentStudentId();
        TopicResponse response = topicService.create(subjectId, request, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponse> updateTopic(
            @PathVariable UUID subjectId,
            @PathVariable UUID id,
            @Valid @RequestBody TopicRequest request) {
        UUID studentId = getCurrentStudentId();
        return ResponseEntity.ok(topicService.update(subjectId, id, request, studentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable UUID subjectId, @PathVariable UUID id) {
        UUID studentId = getCurrentStudentId();
        topicService.delete(subjectId, id, studentId);
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
