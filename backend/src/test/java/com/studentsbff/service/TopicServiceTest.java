package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.studentsbff.dto.TopicRequest;
import com.studentsbff.dto.TopicResponse;
import com.studentsbff.mapper.TopicMapper;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.Subject;
import com.studentsbff.model.Topic;
import com.studentsbff.model.User;
import com.studentsbff.repository.SubjectRepository;
import com.studentsbff.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectService subjectService;

    @Mock
    private TopicMapper topicMapper;

    @InjectMocks
    private TopicService topicService;

    private UUID studentId;
    private Student student;
    private Subject subject;
    private Topic topic;
    private TopicResponse topicResponse;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        User user =
                User.builder()
                        .id(UUID.randomUUID())
                        .name("Test User")
                        .email("test@example.com")
                        .role(Role.STUDENT)
                        .build();
        student = Student.builder().id(studentId).user(user).build();

        UUID subjectId = UUID.randomUUID();
        Instant now = Instant.now();
        subject =
                Subject.builder()
                        .id(subjectId)
                        .name("Mathematics")
                        .student(student)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

        UUID topicId = UUID.randomUUID();
        topic =
                Topic.builder()
                        .id(topicId)
                        .name("Algebra")
                        .subject(subject)
                        .difficulty(3)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
        topicResponse = new TopicResponse(topicId, "Algebra", 3, now, now);
    }

    @Test
    void shouldFindAllBySubjectId() {
        when(subjectRepository.findById(subject.getId())).thenReturn(Optional.of(subject));
        when(topicRepository.findAllBySubjectId(subject.getId())).thenReturn(List.of(topic));
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        List<TopicResponse> result =
                topicService.findAllBySubjectId(subject.getId(), studentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Algebra");
    }

    @Test
    void shouldCreateTopic() {
        TopicRequest request = new TopicRequest();
        request.setName("Geometry");
        request.setDifficulty(4);

        when(subjectRepository.findById(subject.getId())).thenReturn(Optional.of(subject));
        when(topicRepository.save(any(Topic.class))).thenReturn(topic);
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        TopicResponse result = topicService.create(subject.getId(), request, studentId);

        assertThat(result).isNotNull();
        verify(topicRepository).save(any(Topic.class));
    }

    @Test
    void shouldCreateTopicWithDefaultDifficulty() {
        TopicRequest request = new TopicRequest();
        request.setName("Geometry");
        // difficulty is null, should default to 3

        when(subjectRepository.findById(subject.getId())).thenReturn(Optional.of(subject));
        when(topicRepository.save(any(Topic.class))).thenReturn(topic);
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        TopicResponse result = topicService.create(subject.getId(), request, studentId);

        assertThat(result).isNotNull();
        verify(topicRepository).save(any(Topic.class));
    }

    @Test
    void shouldThrowWhenSubjectNotFound() {
        UUID unknownSubjectId = UUID.randomUUID();
        when(subjectRepository.findById(unknownSubjectId)).thenReturn(Optional.empty());

        TopicRequest request = new TopicRequest();
        request.setName("Test");

        assertThatThrownBy(
                        () -> topicService.create(unknownSubjectId, request, studentId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeleteTopic() {
        when(subjectRepository.findById(subject.getId())).thenReturn(Optional.of(subject));
        when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));

        topicService.delete(subject.getId(), topic.getId(), studentId);

        verify(topicRepository).delete(topic);
    }
}
