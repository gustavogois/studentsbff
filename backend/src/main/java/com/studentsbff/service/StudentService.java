package com.studentsbff.service;

import com.studentsbff.dto.StudentProfileRequest;
import com.studentsbff.dto.StudentProfileResponse;
import com.studentsbff.mapper.StudentMapper;
import com.studentsbff.model.Student;
import com.studentsbff.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getProfile(UUID studentId) {
        Student student =
                studentRepository
                        .findById(studentId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Student not found with id: " + studentId));
        return studentMapper.toProfileResponse(student);
    }

    @Transactional
    public StudentProfileResponse updateProfile(UUID studentId, StudentProfileRequest request) {
        Student student =
                studentRepository
                        .findById(studentId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Student not found with id: " + studentId));
        student.setGrade(request.getGrade());
        student.setSchool(request.getSchool());
        Student saved = studentRepository.save(student);
        return studentMapper.toProfileResponse(saved);
    }
}
