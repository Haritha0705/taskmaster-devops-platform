package com.taskmaster.model;

import com.taskmaster.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple model/DTO used by the user feature for internal/service layer operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String profileImageUrl;
    private UserRole role;
    private Boolean isActive;
}
