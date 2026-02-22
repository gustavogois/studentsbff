package com.studentsbff.service;

import com.studentsbff.dto.TopicRequest;
import com.studentsbff.dto.TopicResponse;
import com.studentsbff.mapper.TopicMapper;
import com.studentsbff.model.Subject;
import com.studentsbff.model.Topic;
import com.studentsbff.repository.SubjectRepository;
import com.studentsbff.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectService subjectService;
    private final TopicMapper topicMapper;

    public TopicService(
            TopicRepository topicRepository,
            SubjectRepository subjectRepository,
            SubjectService subjectService,
            TopicMapper topicMapper) {
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
        this.subjectService = subjectService;
        this.topicMapper = topicMapper;
    }

    @Transactional(readOnly = true)
    public List<TopicResponse> findAllBySubjectId(UUID subjectId, UUID studentId) {
        Subject subject = getSubjectAndValidateOwnership(subjectId, studentId);
        return topicRepository.findAllBySubjectId(subject.getId()).stream()
                .map(topicMapper::toResponse)
                .toList();
    }

    @Transactional
    public TopicResponse create(UUID subjectId, TopicRequest request, UUID studentId) {
        Subject subject = getSubjectAndValidateOwnership(subjectId, studentId);

        int difficulty = (request.getDifficulty() != null) ? request.getDifficulty() : 3;

        Topic topic =
                Topic.builder()
                        .name(request.getName())
                        .subject(subject)
                        .difficulty(difficulty)
                        .build();
        Topic saved = topicRepository.save(topic);
        return topicMapper.toResponse(saved);
    }

    @Transactional
    public TopicResponse update(
            UUID subjectId, UUID topicId, TopicRequest request, UUID studentId) {
        getSubjectAndValidateOwnership(subjectId, studentId);

        Topic topic =
                topicRepository
                        .findById(topicId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Topic not found with id: " + topicId));

        if (!topic.getSubject().getId().equals(subjectId)) {
            throw new AccessDeniedException("Topic does not belong to this subject");
        }

        topic.setName(request.getName());
        if (request.getDifficulty() != null) {
            topic.setDifficulty(request.getDifficulty());
        }

        Topic saved = topicRepository.save(topic);
        return topicMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID subjectId, UUID topicId, UUID studentId) {
        getSubjectAndValidateOwnership(subjectId, studentId);

        Topic topic =
                topicRepository
                        .findById(topicId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Topic not found with id: " + topicId));

        if (!topic.getSubject().getId().equals(subjectId)) {
            throw new AccessDeniedException("Topic does not belong to this subject");
        }

        topicRepository.delete(topic);
    }

    private Subject getSubjectAndValidateOwnership(UUID subjectId, UUID studentId) {
        Subject subject =
                subjectRepository
                        .findById(subjectId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Subject not found with id: " + subjectId));
        subjectService.validateOwnership(subject, studentId);
        return subject;
    }
}
