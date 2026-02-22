package com.studentsbff.mapper;

import com.studentsbff.dto.StudentProfileResponse;
import com.studentsbff.model.Student;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentProfileResponse toProfileResponse(Student student);
}
