package com.studentsbff.mapper;

import com.studentsbff.dto.SubjectResponse;
import com.studentsbff.model.Subject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    SubjectResponse toResponse(Subject subject);
}
