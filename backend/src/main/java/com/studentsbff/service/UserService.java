package com.studentsbff.service;

import com.studentsbff.model.Role;
import com.studentsbff.model.Student;
import com.studentsbff.model.User;
import com.studentsbff.repository.StudentRepository;
import com.studentsbff.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public UserService(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public User findOrCreateOAuthUser(String email, String name, String avatarUrl) {
        return userRepository
                .findByEmail(email)
                .orElseGet(
                        () -> {
                            User newUser =
                                    User.builder()
                                            .name(name)
                                            .email(email)
                                            .avatarUrl(avatarUrl)
                                            .role(Role.STUDENT)
                                            .build();
                            User savedUser = userRepository.save(newUser);

                            Student student =
                                    Student.builder().user(savedUser).build();
                            studentRepository.save(student);

                            return savedUser;
                        });
    }
}
