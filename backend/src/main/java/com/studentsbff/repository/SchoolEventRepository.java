package com.studentsbff.repository;

import com.studentsbff.model.SchoolEvent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolEventRepository extends JpaRepository<SchoolEvent, UUID> {

    List<SchoolEvent> findAllByStudentIdOrderByEventDateAsc(UUID studentId);

    Optional<SchoolEvent> findBySourceEmailId(String sourceEmailId);

    boolean existsBySourceEmailId(String sourceEmailId);
}
