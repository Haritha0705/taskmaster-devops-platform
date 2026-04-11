package com.taskmaster.dto.response;

import com.taskmaster.common.enums.UserRole;

public record UserSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role
) {
    public String fullName() {
        return firstName + " " + lastName;
    }
}