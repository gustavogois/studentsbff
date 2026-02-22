package com.studentsbff.repository;

import com.studentsbff.model.Subject;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    List<Subject> findAllByStudentId(UUID studentId);
}
