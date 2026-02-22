package com.studentsbff.mapper;

import com.studentsbff.dto.UserResponse;
import com.studentsbff.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
