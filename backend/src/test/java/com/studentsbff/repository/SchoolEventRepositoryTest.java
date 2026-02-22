package com.studentsbff.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.studentsbff.model.EventSource;
import com.studentsbff.model.EventType;
import com.studentsbff.model.Role;
import com.studentsbff.model.SchoolEvent;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class SchoolEventRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolEventRepository schoolEventRepository;

    @Test
    void shouldSaveAndFindByStudentId() {
        Student student = createStudent("student@example.com");

        SchoolEvent event =
                SchoolEvent.builder()
                        .student(student)
                        .title("Math Exam")
                        .eventType(EventType.EXAM)
                        .eventDate(Instant.now())
                        .source(EventSource.GMAIL)
                        .sourceEmailId("msg-123")
                        .build();
        schoolEventRepository.save(event);

        List<SchoolEvent> found =
                schoolEventRepository.findAllByStudentIdOrderByEventDateAsc(student.getId());

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Math Exam");
    }

    @Test
    void shouldFindBySourceEmailId() {
        Student student = createStudent("student2@example.com");

        SchoolEvent event =
                SchoolEvent.builder()
                        .student(student)
                        .title("History Assignment")
                        .eventType(EventType.ASSIGNMENT)
                        .eventDate(Instant.now())
                        .source(EventSource.GMAIL)
                        .sourceEmailId("msg-456")
                        .build();
        schoolEventRepository.save(event);

        assertThat(schoolEventRepository.existsBySourceEmailId("msg-456")).isTrue();
        assertThat(schoolEventRepository.existsBySourceEmailId("msg-999")).isFalse();
    }

    @Test
    void shouldReturnEmptyWhenNoEventsForStudent() {
        Student student = createStudent("empty@example.com");

        List<SchoolEvent> found =
                schoolEventRepository.findAllByStudentIdOrderByEventDateAsc(student.getId());

        assertThat(found).isEmpty();
    }

    private Student createStudent(String email) {
        User user = User.builder().name("Test").email(email).role(Role.STUDENT).build();
        User savedUser = userRepository.save(user);
        Student student = Student.builder().user(savedUser).build();
        return studentRepository.save(student);
    }
}
