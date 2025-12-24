package com.taskmaster.mapper;

import com.taskmaster.entity.AuthEntity;
import com.taskmaster.model.AuthModel;

/**
 * Manual mapper between AuthEntity and AuthModel.
 * Kept simple to avoid introducing MapStruct generation complexity.
 */
public class AuthMapper {

    public AuthModel toModel(AuthEntity entity) {
        if (entity == null) return null;
        return AuthModel.builder()
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .build();
    }

    public AuthEntity toEntity(AuthModel model) {
        if (model == null) return null;
        return AuthEntity.builder()
                .username(model.getUsername())
                .email(model.getEmail())
                .password(model.getPassword())
                .build();
    }
}
