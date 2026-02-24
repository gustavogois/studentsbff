package com.studentsbff.service;

import com.studentsbff.dto.SchoolEventRequest;
import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.mapper.SchoolEventMapper;
import com.studentsbff.model.SchoolEvent;
import com.studentsbff.model.Subject;
import com.studentsbff.model.Student;
import com.studentsbff.repository.SchoolEventRepository;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchoolEventService {

    private final SchoolEventRepository schoolEventRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolEventMapper schoolEventMapper;

    public SchoolEventService(
            SchoolEventRepository schoolEventRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            SchoolEventMapper schoolEventMapper) {
        this.schoolEventRepository = schoolEventRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.schoolEventMapper = schoolEventMapper;
    }

    @Transactional(readOnly = true)
    public List<SchoolEventResponse> findAll(UUID studentId, LocalDate from, LocalDate to) {
        List<SchoolEvent> events;
        if (from != null && to != null) {
            events =
                    schoolEventRepository.findAllByStudentIdAndEventDateBetween(
                            studentId, from, to);
        } else {
            events = schoolEventRepository.findAllByStudentId(studentId);
        }
        return events.stream().map(schoolEventMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SchoolEventResponse findById(UUID eventId, UUID studentId) {
        SchoolEvent event =
                schoolEventRepository
                        .findById(eventId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("School event not found"));
        validateOwnership(event, studentId);
        return schoolEventMapper.toResponse(event);
    }

    @Transactional
    public SchoolEventResponse create(SchoolEventRequest request, UUID studentId) {
        Student student =
                studentRepository
                        .findById(studentId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Student not found"));

        SchoolEvent event =
                SchoolEvent.builder()
                        .student(student)
                        .title(request.getTitle())
                        .eventType(request.getEventType())
                        .eventDate(request.getEventDate())
                        .description(request.getDescription())
                        .build();

        if (request.getSubjectId() != null) {
            Subject subject =
                    subjectRepository
                            .findById(request.getSubjectId())
                            .orElseThrow(
                                    () ->
                                            new EntityNotFoundException(
                                                    "Subject not found"));
            event.setSubject(subject);
        }

        SchoolEvent saved = schoolEventRepository.save(event);
        return schoolEventMapper.toResponse(saved);
    }

    @Transactional
    public SchoolEventResponse update(
            UUID eventId, SchoolEventRequest request, UUID studentId) {
        SchoolEvent event =
                schoolEventRepository
                        .findById(eventId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("School event not found"));
        validateOwnership(event, studentId);

        event.setTitle(request.getTitle());
        event.setEventType(request.getEventType());
        event.setEventDate(request.getEventDate());
        event.setDescription(request.getDescription());

        if (request.getSubjectId() != null) {
            Subject subject =
                    subjectRepository
                            .findById(request.getSubjectId())
                            .orElseThrow(
                                    () ->
                                            new EntityNotFoundException(
                                                    "Subject not found"));
            event.setSubject(subject);
        } else {
            event.setSubject(null);
        }

        SchoolEvent saved = schoolEventRepository.save(event);
        return schoolEventMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID eventId, UUID studentId) {
        SchoolEvent event =
                schoolEventRepository
                        .findById(eventId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("School event not found"));
        validateOwnership(event, studentId);
        schoolEventRepository.delete(event);
    }

    private void validateOwnership(SchoolEvent event, UUID studentId) {
        if (!event.getStudent().getId().equals(studentId)) {
            throw new AccessDeniedException(
                    "You do not have access to this school event");
        }
    }
}
