package com.studentsbff.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.Subject;
import com.studentsbff.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class SubjectRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Test
    void shouldFindAllByStudentId() {
        User user =
                User.builder()
                        .name("Student User")
                        .email("student@example.com")
                        .role(Role.STUDENT)
                        .build();
        User savedUser = userRepository.save(user);

        Student student = Student.builder().user(savedUser).build();
        Student savedStudent = studentRepository.save(student);

        Subject subject1 =
                Subject.builder().name("Mathematics").student(savedStudent).build();
        Subject subject2 =
                Subject.builder().name("Science").student(savedStudent).build();
        subjectRepository.save(subject1);
        subjectRepository.save(subject2);

        List<Subject> found = subjectRepository.findAllByStudentId(savedStudent.getId());

        assertThat(found).hasSize(2);
        assertThat(found).extracting(Subject::getName).containsExactlyInAnyOrder("Mathematics", "Science");
    }
}
