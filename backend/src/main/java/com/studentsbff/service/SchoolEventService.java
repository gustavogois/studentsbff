package com.studentsbff.service;

import com.studentsbff.dto.ParsedSchoolEvent;
import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.mapper.SchoolEventMapper;
import com.studentsbff.model.EventSource;
import com.studentsbff.model.EventType;
import com.studentsbff.model.SchoolEvent;
import com.studentsbff.model.Student;
import com.studentsbff.model.Subject;
import com.studentsbff.repository.SchoolEventRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchoolEventService {

    private final SchoolEventRepository schoolEventRepository;
    private final SchoolEventMapper schoolEventMapper;

    public SchoolEventService(
            SchoolEventRepository schoolEventRepository, SchoolEventMapper schoolEventMapper) {
        this.schoolEventRepository = schoolEventRepository;
        this.schoolEventMapper = schoolEventMapper;
    }

    @Transactional(readOnly = true)
    public List<SchoolEventResponse> findAllByStudentId(UUID studentId) {
        return schoolEventRepository.findAllByStudentIdOrderByEventDateAsc(studentId).stream()
                .map(schoolEventMapper::toResponse)
                .toList();
    }

    @Transactional
    public void delete(UUID eventId, UUID studentId) {
        SchoolEvent event =
                schoolEventRepository
                        .findById(eventId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "School event not found with id: " + eventId));
        if (!event.getStudent().getId().equals(studentId)) {
            throw new AccessDeniedException("You do not have access to this event");
        }
        schoolEventRepository.delete(event);
    }

    @Transactional
    public List<SchoolEventResponse> saveFromParsed(
            List<ParsedSchoolEvent> parsed, Student student, List<Subject> subjects) {
        Map<String, Subject> subjectByName =
                subjects.stream()
                        .collect(
                                Collectors.toMap(
                                        s -> s.getName().toLowerCase(),
                                        Function.identity(),
                                        (a, b) -> a));

        return parsed.stream()
                .map(
                        p -> {
                            Subject matchedSubject = null;
                            if (p.relatedSubjectName() != null) {
                                matchedSubject =
                                        subjectByName.get(
                                                p.relatedSubjectName().toLowerCase());
                            }

                            SchoolEvent event =
                                    SchoolEvent.builder()
                                            .student(student)
                                            .title(p.title())
                                            .eventType(EventType.valueOf(p.eventType()))
                                            .subject(matchedSubject)
                                            .description(p.description())
                                            .eventDate(parseEventDate(p.eventDate()))
                                            .source(EventSource.GMAIL)
                                            .sourceEmailId(p.sourceEmailId())
                                            .build();

                            return schoolEventMapper.toResponse(
                                    schoolEventRepository.save(event));
                        })
                .toList();
    }

    private Instant parseEventDate(String dateStr) {
        try {
            return Instant.parse(dateStr);
        } catch (Exception e) {
            return Instant.now();
        }
    }
}
