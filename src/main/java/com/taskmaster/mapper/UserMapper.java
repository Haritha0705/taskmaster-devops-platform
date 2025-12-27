package com.taskmaster.mapper;

import com.taskmaster.dto.request.UserCreateRequest;
import com.taskmaster.dto.request.UserUpdateRequest;
import com.taskmaster.dto.response.UserResponse;
import com.taskmaster.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for User entity
 */

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = { TaskMapper.class } // use TaskMapper to map TaskEntity -> TaskResponse
)
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "tasks", source = "tasks")
    UserResponse toResponse(UserEntity user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    UserEntity toEntity(UserCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "isAccountLocked", ignore = true)
    void updateEntityFromRequest(
            UserUpdateRequest request,
            @MappingTarget UserEntity user
    );
}
