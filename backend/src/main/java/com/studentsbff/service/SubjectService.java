package com.studentsbff.service;

import com.studentsbff.dto.SubjectRequest;
import com.studentsbff.dto.SubjectResponse;
import com.studentsbff.mapper.SubjectMapper;
import com.studentsbff.model.Student;
import com.studentsbff.model.Subject;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final SubjectMapper subjectMapper;

    public SubjectService(
            SubjectRepository subjectRepository,
            StudentRepository studentRepository,
            SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.subjectMapper = subjectMapper;
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findAllByStudentId(UUID studentId) {
        return subjectRepository.findAllByStudentId(studentId).stream()
                .map(subjectMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse findById(UUID id, UUID studentId) {
        Subject subject =
                subjectRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Subject not found with id: " + id));
        validateOwnership(subject, studentId);
        return subjectMapper.toResponse(subject);
    }

    @Transactional
    public SubjectResponse create(SubjectRequest request, UUID studentId) {
        Student student =
                studentRepository
                        .findById(studentId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Student not found with id: " + studentId));

        Subject subject = Subject.builder().name(request.getName()).student(student).build();
        Subject saved = subjectRepository.save(subject);
        return subjectMapper.toResponse(saved);
    }

    @Transactional
    public SubjectResponse update(UUID id, SubjectRequest request, UUID studentId) {
        Subject subject =
                subjectRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Subject not found with id: " + id));
        validateOwnership(subject, studentId);
        subject.setName(request.getName());
        Subject saved = subjectRepository.save(subject);
        return subjectMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id, UUID studentId) {
        Subject subject =
                subjectRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Subject not found with id: " + id));
        validateOwnership(subject, studentId);
        subjectRepository.delete(subject);
    }

    void validateOwnership(Subject subject, UUID studentId) {
        if (!subject.getStudent().getId().equals(studentId)) {
            throw new AccessDeniedException("You do not have access to this subject");
        }
    }
}
