package com.studentsbff.mapper;

import com.studentsbff.dto.SchoolEventResponse;
import com.studentsbff.model.SchoolEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SchoolEventMapper {

    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "subject.name", target = "subjectName")
    @Mapping(source = "eventType", target = "eventType")
    @Mapping(source = "source", target = "source")
    SchoolEventResponse toResponse(SchoolEvent schoolEvent);
}
