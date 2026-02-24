package com.studentsbff.mapper;

import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.model.SchoolEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SchoolEventMapper {

    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "subject.name", target = "subjectName")
    SchoolEventResponse toResponse(SchoolEvent event);
}
