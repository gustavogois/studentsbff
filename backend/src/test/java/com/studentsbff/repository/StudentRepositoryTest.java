package com.studentsbff.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.studentsbff.model.Grade;
import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void shouldSaveAndFindByUserId() {
        User user =
                User.builder()
                        .name("Student User")
                        .email("student@example.com")
                        .role(Role.STUDENT)
                        .build();
        User savedUser = userRepository.save(user);

        Student student =
                Student.builder()
                        .user(savedUser)
                        .grade(Grade.GRADE_8)
                        .school("Test School")
                        .turma("A")
                        .build();
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findByUserId(savedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(found.get().getGrade()).isEqualTo(Grade.GRADE_8);
        assertThat(found.get().getSchool()).isEqualTo("Test School");
        assertThat(found.get().getTurma()).isEqualTo("A");
    }
}
