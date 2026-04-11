package com.taskmaster.mapper;

import com.taskmaster.dto.request.UserCreateRequest;
import com.taskmaster.dto.request.UserUpdateRequest;
import com.taskmaster.dto.response.UserDetailResponse;
import com.taskmaster.dto.response.UserSummaryResponse;
import com.taskmaster.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = { TaskMapper.class }
)
public interface UserMapper {

    @Mapping(target = "tasks", source = "tasks")
    UserDetailResponse toDetailResponse(UserEntity user);

    UserSummaryResponse toSummaryResponse(UserEntity user);

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
