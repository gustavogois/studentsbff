package com.studentsbff.repository;

import com.studentsbff.model.SchoolEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolEventRepository extends JpaRepository<SchoolEvent, UUID> {

    List<SchoolEvent> findAllByStudentId(UUID studentId);

    List<SchoolEvent> findAllByStudentIdAndEventDateBetween(
            UUID studentId, LocalDate from, LocalDate to);
}
