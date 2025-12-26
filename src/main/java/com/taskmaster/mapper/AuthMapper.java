package com.taskmaster.mapper;

import com.taskmaster.dto.request.RegisterRequest;
import com.taskmaster.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AuthMapper {

    UserEntity toUserEntity(RegisterRequest request);
}
