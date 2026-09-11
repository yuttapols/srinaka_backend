package com.srinaka.user.mapper;

import com.srinaka.user.dto.UserResponse;
import com.srinaka.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
