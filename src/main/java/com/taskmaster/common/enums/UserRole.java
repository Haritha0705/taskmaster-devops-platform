package com.taskmaster.common.enums;

import lombok.Getter;

/**
 * User roles in the system
 */
@Getter
public enum UserRole {
    ROLE_USER("User"),
    ROLE_ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

}
