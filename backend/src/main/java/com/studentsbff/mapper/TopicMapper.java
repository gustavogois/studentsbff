package com.studentsbff.mapper;

import com.studentsbff.dto.TopicResponse;
import com.studentsbff.model.Topic;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    TopicResponse toResponse(Topic topic);
}
