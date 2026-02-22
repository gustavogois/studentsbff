package com.studentsbff.repository;

import com.studentsbff.model.ParentStudent;
import com.studentsbff.model.ParentStudentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentStudentRepository extends JpaRepository<ParentStudent, ParentStudentId> {}
