package com.taskmaster.dto.response;

import com.taskmaster.common.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for user response (Full API)
 */
public record UserDetailResponse(

        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        String profileImageUrl,
        UserRole role,

        List<TaskResponse> tasks,

        Boolean isEmailVerified,
        Boolean isActive,
        Boolean isDeleted,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public String fullName() {
        return firstName + " " + lastName;
    }
}